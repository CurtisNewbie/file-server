package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.exceptions.*;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.config.FileServiceConfig;
import com.yongj.converters.FileSharingConverter;
import com.yongj.converters.TagConverter;
import com.yongj.dao.*;
import com.yongj.enums.*;
import com.yongj.helper.FsGroupIdResolver;
import com.yongj.helper.WriteFsGroupSupplier;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.io.ZipCompressEntry;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.util.AssertUtils.*;
import static com.curtisnewbie.common.util.ExceptionUtils.illegalState;
import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageableList;
import static com.yongj.util.IOUtils.ioThreadPool;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {

    public static final String FILE_LOCK_PREFIX = "file:uuid:";

    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private FileSharingMapper fileSharingMapper;
    @Autowired
    private FileSharingConverter fileSharingConverter;
    @Autowired
    private FileTagMapper fileTagMapper;
    @Autowired
    private TagMapper tagMapper;
    @Autowired
    private RedisController redisController;
    @Autowired
    private TagConverter tagConverter;
    @Autowired
    private WriteFsGroupSupplier writeFsGroupSupplier;
    @Autowired
    private FsGroupIdResolver fsGroupIdResolver;
    @Autowired
    private FileServiceConfig fileServiceConfig;

    @Override
    public void grantFileAccess(@NotNull GrantFileAccessCmd cmd) {
        // check if the grantedTo is the uploader
        AssertUtils.notEquals(cmd.getGrantedTo(), cmd.getGrantedByUserId(), "You can't grant file access to yourself");

        // make sure the file exists
        final LambdaQueryWrapper<FileInfo> fQry = new LambdaQueryWrapper<>();
        fQry.select(FileInfo::getId, FileInfo::getUploaderId)
                .eq(FileInfo::getId, cmd.getFileId())
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue());
        final FileInfo file = fileInfoMapper.selectOne(fQry);
        nonNull(file, "File not found");

        // only uploader can grant access to the file
        AssertUtils.equals((int) file.getUploaderId(), cmd.getGrantedByUserId(), "Only uploader can grant access to the file");

        // check if the user already had access to the file
        final LambdaQueryWrapper<FileSharing> fsQry = new LambdaQueryWrapper<>();
        fsQry.select(FileSharing::getId, FileSharing::getIsDel)
                .eq(FileSharing::getFileId, cmd.getFileId())
                .eq(FileSharing::getUserId, cmd.getGrantedTo());
        final FileSharing fileSharing = fileSharingMapper.selectOne(fsQry);
        isTrue(fileSharing == null || fileSharing.getIsDel() == IsDel.DELETED,
                "User already had access to this file");

        if (fileSharing == null) {
            // insert file_sharing record
            FileSharing fs = new FileSharing();
            fs.setUserId(cmd.getGrantedTo());
            fs.setFileId(cmd.getFileId());
            fileSharingMapper.insert(fs);
        } else {
            // update is_del to false
            FileSharing updateParam = new FileSharing();
            updateParam.setId(fileSharing.getId());
            updateParam.setIsDel(IsDel.NORMAL);
            fileSharingMapper.updateById(updateParam);
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CompletableFuture<FileInfo> uploadFile(UploadFileVo param) {
        final SingleUploadChainHelper chainHelper = new SingleUploadChainHelper();
        chainHelper.uuid = UUID.randomUUID().toString();
        chainHelper.uploaderId = param.getUserId();
        chainHelper.inputStream = param.getInputStream();
        chainHelper.fileName = param.getFileName();
        chainHelper.uploaderName = param.getUsername();
        chainHelper.userGroup = param.getUserGroup();

        return CompletableFuture.supplyAsync(() -> resolveFsGroup(chainHelper), ioThreadPool)
                .thenApply(this::resolveAbsPath)
                .thenApply(this::uploadSingleFile)
                .thenApply(this::saveFileInfo);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public CompletableFuture<FileInfo> uploadFilesAsZip(final UploadZipFileVo param) throws IOException {
        final int userId = param.getUserId();
        final String zipFile = param.getZipFile();
        final FileUserGroupEnum userGroup = param.getUserGroup();
        final MultipartFile[] multipartFiles = param.getMultipartFiles();

        nonNull(userGroup);
        hasText(zipFile);
        notEmpty(multipartFiles);

        final int maxZipEtry = fileServiceConfig.getMaxZipEntries();
        AssertUtils.isTrue(multipartFiles.length < maxZipEtry, "You can at most compress %s zip entries", maxZipEtry);

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();

        final FsGroup fsGroup = writeFsGroupSupplier.supply(FsGroupType.USER);
        nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, userId, fsGroup.getBaseFolder());

        // prepare zip entries
        final List<ZipCompressEntry> entries = prepareZipEntries(multipartFiles);

        // write zip file (not making it async is because sometimes the files get deleted before compression)
        final long sizeInBytes = ioHandler.writeZipFile(absPath, entries);

        return CompletableFuture.supplyAsync(() -> {
            // save file info record
            FileInfo f = new FileInfo();
            f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
            f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
            f.setName(zipFile.endsWith(".zip") ? zipFile : zipFile + ".zip");
            f.setUploaderId(userId);
            f.setUploadTime(LocalDateTime.now());
            f.setUploaderName(param.getUsername());
            f.setUuid(uuid);
            f.setUserGroup(userGroup.getValue());
            f.setSizeInBytes(sizeInBytes);
            f.setFsGroupId(fsGroup.getId());
            fileInfoMapper.insert(f);
            return f;
        });
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageableList<FileInfoVo> findPagedFilesForUser(@NotNull ListFileInfoReqVo reqVo) {
        SelectFileInfoListParam param = BeanCopyUtils.toType(reqVo, SelectFileInfoListParam.class);
        if (reqVo.filterForOwnedFilesOnly()) {
            param.setFilterOwnedFiles(true);
        }
        final Page<?> p = forPage(reqVo.getPagingVo());

        /*
            Based on whether tagName is present, we use different queries

            Instead of using the Page<?> for pagination, we do COUNT(*) manually,
            the paginator plugin always fails to optimise the query :D
         */
        final boolean qryForTag = StringUtils.hasText(param.getTagName());
        List<FileInfo> dataList = qryForTag ?
                fileInfoMapper.selectFileListForUserAndTag(p, param.getUserId(), param.getTagName(), param.getFilename()) :
                fileInfoMapper.selectFileListForUserSelective(p, param);
        final long count = qryForTag ?
                fileInfoMapper.countFileListForUserAndTag(param.getUserId(), param.getTagName(), param.getFilename()) :
                fileInfoMapper.countFileListForUserSelective(param);

        final List<FileInfoVo> converted = dataList.stream().map(e -> {
            FileInfoVo v = BeanCopyUtils.toType(e, FileInfoVo.class);
            v.setIsOwner(Objects.equals(e.getUploaderId(), reqVo.getUserId()));
            return v;
        }).collect(Collectors.toList());

        final PageableList<FileInfoVo> pl = new PageableList<>();
        pl.setPayload(converted);
        pl.setPagingVo(PagingUtil.ofPageAndTotal((int) p.getCurrent(), count));
        return pl;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<PhysicDeleteFileVo> findPagedFileIdsForPhysicalDeleting() {
        List<FileInfo> dataList = fileInfoMapper.findInfoForPhysicalDeleting();
        return BeanCopyUtils.toTypeList(dataList, PhysicDeleteFileVo.class);
    }

    @Override
    public PageableList<FileUploaderInfoVo> findPagedFilesWithoutUploaderName(@NotNull PagingVo pagingVo) {
        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .select("id", "uploader_id")
                .eq("uploader_name", "");

        final IPage<FileInfo> dataList = fileInfoMapper.selectPage(forPage(pagingVo), cond);
        return toPageableList(dataList, (f) ->
                FileUploaderInfoVo.builder()
                        .id(f.getId())
                        .uploaderId(f.getUploaderId())
                        .build());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FileInfo findById(int id) {
        QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .eq("id", id)
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue())
                .eq("is_del", IsDel.NORMAL.getValue());

        return fileInfoMapper.selectOne(cond);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FileInfo findByKey(String uuid) {
        LambdaQueryWrapper<FileInfo> cond = new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getUuid, uuid)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                .eq(FileInfo::getIsDel, IsDel.NORMAL.getValue());

        return fileInfoMapper.selectOne(cond);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public InputStream retrieveFileInputStream(int id) throws IOException {
        return Files.newInputStream(resolveFilePath(id));
    }

    @Override
    public FileChannel retrieveFileChannel(int id) throws IOException {
        return FileChannel.open(resolveFilePath(id), StandardOpenOption.READ);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public InputStream retrieveFileInputStream(String uuid) throws IOException {
        LambdaQueryWrapper<FileInfo> w = new LambdaQueryWrapper<FileInfo>()
                .select(FileInfo::getId)
                .eq(FileInfo::getUuid, uuid);

        Integer id = fileInfoMapper.selectAndConvert(w, FileInfo::getId);
        AssertUtils.notNull(id, "File not found, key: %s", uuid);

        return retrieveFileInputStream(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void validateUserDownload(int userId, int fileId, String userNo) {
        final FileDownloadValidInfo f = fileInfoMapper.selectValidateInfoById(fileId, userId, userNo);
        nonNull(f, "File is not found");
        isFalse(f.isDeleted(), "File is deleted");
        isTrue(f.isNotDir(), "Downloading a directory is not supported");

        // current user is the uploader
        if (Objects.equals(f.getUploaderId(), userId)) return;

        // file shared by the uploader
        if (f.getFileSharingId() != null && f.getFileSharingId() > 0) return;

        // file belongs to a folder that current user has access to
        if (fileInfoMapper.selectAnyUserFolderIdForFile(fileId, userNo) != null) return;

        throw new UnrecoverableException("You are not allowed to download this file");
    }

    @Override
    public void moveFileInto(int userId, String uuid, String parentFileUuid) {
        LockUtils.lockAndRun(getFileLock(uuid), () -> {
            LockUtils.lockAndRun(getFileLock(parentFileUuid), () -> {

                final FileInfo f = fileInfoMapper.selectOne(Wrappers.lambdaQuery(FileInfo.class)
                        .eq(FileInfo::getUuid, uuid)
                        .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL));
                nonNull(f, "Record not found");
                isFalse(f.isDir(), "Directory can't be moved into another directory");
                AssertUtils.equals(userId, (int) f.getUploaderId(), "Only the uploader can move files");

                final FileInfo dir = fileInfoMapper.selectOne(Wrappers.lambdaQuery(FileInfo.class)
                        .eq(FileInfo::getUuid, parentFileUuid)
                        .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL));
                nonNull(dir, "Directory not found");
                isTrue(dir.isDir(), "Target file is not a directory");
                AssertUtils.equals(userId, (int) dir.getUploaderId(), "You are not the owner of this directory");

                fileInfoMapper.update(null, Wrappers.lambdaUpdate(FileInfo.class)
                        .set(FileInfo::getParentFile, parentFileUuid)
                        .eq(FileInfo::getUuid, uuid));
            });
        });
    }

    @Override
    public void deleteFileLogically(int userId, String uuid) {
        LockUtils.lockAndRun(getFileLock(uuid), () -> {

            final FileInfo f = fileInfoMapper.selectOne(Wrappers.lambdaQuery(FileInfo.class)
                    .select(FileInfo::getId, FileInfo::getUploaderId, FileInfo::getFileType)
                    .eq(FileInfo::getUuid, uuid)
                    .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL));

            nonNull(f, "Record not found");
            AssertUtils.equals(userId, (int) f.getUploaderId(), "You can only delete files that you uploaded");

            // check file type
            if (f.isDir()) {
                // if it's dir make sure it's empty
                final boolean isEmpty = fileInfoMapper.selectOne(new LambdaQueryWrapper<FileInfo>()
                        .select(FileInfo::getId)
                        .eq(FileInfo::getParentFile, uuid)
                        .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                        .last("limit 1")) == null;

                AssertUtils.isTrue(isEmpty, "Directory is not empty, unable to delete it");
            }

            // mark logically deleted
            fileInfoMapper.logicDelete(f.getId());
        });
    }

    @Override
    public void markFileDeletedPhysically(int id) {
        fileInfoMapper.markFilePhysicDeleted(id, LocalDateTime.now());
    }

    @Override
    public void updateFile(@NotNull UpdateFileCmd cmd) {
        Integer uploaderId = fileInfoMapper.selectUploaderIdById(cmd.getId());
        nonNull(uploaderId, "Record not found");
        AssertUtils.equals((int) uploaderId, cmd.getUpdatedById(), "You are not allowed to update this file");

        FileInfo fi = new FileInfo();
        fi.setId(cmd.getId());
        fi.setUserGroup(cmd.getUserGroup().getValue());
        fi.setName(cmd.getFileName());

        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .eq("id", cmd.getId())
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue())
                .eq("is_del", IsDel.NORMAL.getValue());

        fileInfoMapper.update(fi, cond);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageableList<FileSharingVo> listGrantedAccess(int fileId, int requestUserId, @NotNull PagingVo paging) {
        Assert.isTrue(isFileOwner(requestUserId, fileId), "Only uploader can list granted access");

        LambdaQueryWrapper<FileSharing> condition = new LambdaQueryWrapper<>();
        condition.select(FileSharing::getId, FileSharing::getUserId, FileSharing::getCreateTime, FileSharing::getCreateBy)
                .eq(FileSharing::getFileId, fileId)
                .eq(FileSharing::getIsDel, IsDel.NORMAL)
                .orderByDesc(FileSharing::getId);
        Page page = forPage(paging);
        return toPageableList(fileSharingMapper.selectPage(page, condition), fileSharingConverter::toVo);
    }

    @Override
    public void removeGrantedAccess(int fileId, int userId, int removedByUserId) {
        Assert.isTrue(isFileOwner(removedByUserId, fileId), "Only uploader can remove granted access");

        FileSharing updateParam = new FileSharing();
        updateParam.setIsDel(IsDel.DELETED);

        QueryWrapper<FileSharing> whereCondition = new QueryWrapper<>();
        whereCondition
                .eq("file_id", fileId)
                .eq("user_id", userId)
                .eq("is_del", IsDel.NORMAL);
        fileSharingMapper.update(updateParam, whereCondition);
    }

    @Override
    public void fillBlankUploaderName(int fileId, @NotNull String uploaderName) {
        final FileInfo updateParam = new FileInfo();
        updateParam.setUploaderName(uploaderName);

        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .eq("id", fileId)
                .eq("uploader_name", ""); // make sure we don't accidentally overwrite previous name

        fileInfoMapper.update(updateParam, cond);
    }

    @Override
    public void tagFile(final TagFileCmd cmd) {
        final String tagName = cmd.getTagName().trim();
        final int fileId = cmd.getFileId();
        final int userId = cmd.getUserId();

        // tag the file with lock
        final Lock lock = getFileTagLock(userId, tagName);
        try {
            lock.lock();

            // find the tag first, and create one for current user if necessary
            final int tagId = createTagIfNecessary(userId, tagName);

            // check if it's already tagged
            final FileTag selected = selectFileTag(fileId, tagId);

            // insert one if it doesn't exist
            if (selected == null) {
                FileTag inserted = new FileTag();
                inserted.setUserId(userId);
                inserted.setFileId(fileId);
                inserted.setTagId(tagId);
                fileTagMapper.insert(inserted);
                return;
            }

            // reset, if it's deleted
            if (selected.isDeleted()) {
                final FileTag updated = new FileTag();
                updated.setIsDel(IsDel.NORMAL);
                final QueryWrapper<FileTag> where = new QueryWrapper<FileTag>()
                        .eq("id", selected.getId())
                        .eq("is_del", IsDel.DELETED.getValue());
                fileTagMapper.update(updated, where);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void untagFile(@NotNull UntagFileCmd cmd) {
        final String tagName = cmd.getTagName();
        final int fileId = cmd.getFileId();
        final int userId = cmd.getUserId();

        final Lock lock = getFileTagLock(userId, tagName);
        try {
            lock.lock();

            // each tag is bound to a specific user
            Tag tag = selectTag(userId, tagName);
            if (tag == null) {
                log.info("Tag for '{}' doesn't exist, unable to untag file", tagName);
                return;
            }
            final int tagId = tag.getId();

            final FileTag fileTag = selectFileTag(fileId, tag.getId());
            if (fileTag == null) {
                log.info("FileTag for file_id: {}, tag_id: {} doesn't exist", fileId, tag.getId());
                return;
            }

            // todo we don't delete the tag here for now
            // set as deleted
            if (!fileTag.isDeleted()) {
                final FileTag updated = new FileTag();
                updated.setIsDel(IsDel.DELETED);
                final QueryWrapper<FileTag> where = new QueryWrapper<FileTag>()
                        .eq("id", fileTag.getId())
                        .eq("is_del", IsDel.NORMAL.getValue());

                if (fileTagMapper.update(updated, where) > 0) {
                    log.info("Untagged file, file_id: {}, tag_name: {}", fileId, tagName);

                    /*
                        check if the tag is still associated with other files, if not, we remove it
                        remember, the tag is bound for a specific user only, so this doesn't affect
                        other users
                     */
                    Integer anyId = fileTagMapper.selectAndConvert(new LambdaQueryWrapper<FileTag>()
                            .select(FileTag::getId)
                            .eq(FileTag::getTagId, tagId)
                            .eq(FileTag::getIsDel, IsDel.NORMAL)
                            .last("limit 1"), FileTag::getId);
                    if (anyId == null) {
                        Tag ut = new Tag();
                        ut.setIsDel(IsDel.DELETED);
                        ut.setId(tagId);
                        tagMapper.updateById(ut);
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> listFileTags(final int userId) {
        return fileTagMapper.listFileTags(userId);
    }

    @Override
    public PageableList<TagVo> listFileTags(final int userId, final int fileId, final Page<?> page) {
        return toPageableList(fileTagMapper.listTagsForFile(page, userId, fileId), tagConverter::toVo);
    }

    @Override
    public boolean isFileOwner(int userId, int fileId) {
        return fileInfoMapper.selectOne(new LambdaQueryWrapper<FileInfo>()
                .select(FileInfo::getId)
                .eq(FileInfo::getId, fileId)
                .eq(FileInfo::getUploaderId, userId)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                .last("limit 1")) != null;
    }

    @Override
    public boolean isFileOwner(int userId, String uuid) {
        return fileInfoMapper.selectOne(new LambdaQueryWrapper<FileInfo>()
                .select(FileInfo::getId)
                .eq(FileInfo::getUuid, uuid)
                .eq(FileInfo::getUploaderId, userId)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                .last("limit 1")) != null;
    }

    @Override
    public FileType findFileTypeByKey(String uuid) {
        final FileInfo f = fileInfoMapper.selectOne(new LambdaQueryWrapper<FileInfo>()
                .select(FileInfo::getFileType)
                .eq(FileInfo::getUuid, uuid));
        if (f == null) return null;
        return f.getFileType();
    }

    @Override
    public FileInfo mkdir(MakeDirReqVo r) {
        final String uuid = UUID.randomUUID().toString();

        FileInfo dir = new FileInfo();
        dir.setName(r.getName());
        dir.setUuid(uuid);
        dir.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        dir.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        dir.setSizeInBytes(0L);
        dir.setUploaderId(r.getUploaderId());
        dir.setUploaderName(r.getUploaderName());
        dir.setUploadTime(LocalDateTime.now());

        if (r.getUserGroup() == null) r.setUserGroup(FileUserGroupEnum.PRIVATE.getValue());
        else AssertUtils.notNull(FileUserGroupEnum.parse(r.getUserGroup()));

        dir.setUserGroup(r.getUserGroup());
        dir.setFileType(FileType.DIR);

        fileInfoMapper.insert(dir);

        return dir;
    }

    // ------------------------------------- private helper methods ------------------------------------

    private List<ZipCompressEntry> prepareZipEntries(MultipartFile[] multipartFiles) throws IOException {
        List<ZipCompressEntry> l = new ArrayList<>(multipartFiles.length);
        for (final MultipartFile mf : multipartFiles) {
            l.add(new ZipCompressEntry(mf.getOriginalFilename(), mf.getInputStream()));
        }
        return l;
    }

    private FileTag selectFileTag(final int fileId, final int tagId) {
        final QueryWrapper<FileTag> cond = new QueryWrapper<FileTag>()
                .eq("file_id", fileId)
                .eq("tag_id", tagId);
        return fileTagMapper.selectOne(cond);
    }

    private Tag selectTag(final int userId, final String name) {
        final QueryWrapper<Tag> cond = new QueryWrapper<Tag>()
                .eq("user_id", userId)
                .eq("name", name);
        return tagMapper.selectOne(cond);
    }

    /**
     * Create tag for current user if necessary
     * <p>
     * This method requires proper locking and synchronization
     * </p>
     *
     * @param userId  id of user
     * @param tagName name of tag
     * @return id of tag
     */
    private int createTagIfNecessary(final int userId, final String tagName) {
        final Tag selected = selectTag(userId, tagName);
        if (selected == null) {
            Tag inserted = new Tag();
            inserted.setUserId(userId);
            inserted.setName(tagName);
            tagMapper.insert(inserted);
            return inserted.getId();
        }

        // update is_del back to normal
        if (selected.isDeleted()) {
            final Tag updated = new Tag();
            updated.setIsDel(IsDel.NORMAL);

            final QueryWrapper<Tag> cond = new QueryWrapper<Tag>()
                    .eq("id", selected.getId())
                    .eq("is_del", IsDel.DELETED.getValue());
            tagMapper.update(updated, cond);
        }

        return selected.getId();
    }

    private Lock getFileTagLock(int userId, String tagName) {
        return redisController.getLock(String.format("file:tag:uid:%s:name:%s", userId, tagName));
    }

    private Path resolveFilePath(int id) {
        final FileInfo fi = fileInfoMapper.selectDownloadInfoById(id);
        nonNull(fi, "Record not found");

        final FsGroup fsg = fsGroupIdResolver.resolve(fi.getFsGroupId());
        if (fsg == null || fsg.isDeleted())
            throw illegalState("FS Group FS Group for this record is not found");

        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        log.info("Resolved path: '{}' for file.id: {}", absPath, id);
        return Paths.get(absPath);
    }

    private static class SingleUploadChainHelper {
        private FileUserGroupEnum userGroup;
        private String uploaderName;
        private String fileName;
        private String uuid;
        private int uploaderId;
        private FsGroup fsGroup;
        private String absPath;
        private InputStream inputStream;
        private Long sizeInBytes;
    }

    private SingleUploadChainHelper resolveFsGroup(final SingleUploadChainHelper chainHelper) {
        final FsGroup fsGroup = writeFsGroupSupplier.supply(FsGroupType.USER);
        nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");
        chainHelper.fsGroup = fsGroup;
        return chainHelper;
    }

    private SingleUploadChainHelper resolveAbsPath(SingleUploadChainHelper chainHelper) {
        final String absPath = pathResolver.resolveAbsolutePath(chainHelper.uuid, chainHelper.uploaderId,
                chainHelper.fsGroup.getBaseFolder());
        nonNull(absPath, "Unable to resolve absolute path, unable to upload file, please contact administrator");

        chainHelper.absPath = absPath;
        return chainHelper;
    }

    private SingleUploadChainHelper uploadSingleFile(SingleUploadChainHelper chainHelper) {
        final String absPath = chainHelper.absPath;
        try {
            chainHelper.sizeInBytes = ioHandler.writeFile(absPath, chainHelper.inputStream);
        } catch (IOException e) {
            log.error("Failed to write file, {}", absPath, e);
            throw new IllegalStateException("Failed to upload file, unknown error");
        }
        return chainHelper;
    }

    private FileInfo saveFileInfo(SingleUploadChainHelper chainHelper) {
        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(chainHelper.fileName);
        f.setUploaderId(chainHelper.uploaderId);
        f.setUploaderName(chainHelper.uploaderName);
        f.setUploadTime(LocalDateTime.now());
        f.setUuid(chainHelper.uuid);
        f.setUserGroup(chainHelper.userGroup.getValue());
        f.setSizeInBytes(chainHelper.sizeInBytes);
        f.setFsGroupId(chainHelper.fsGroup.getId());
        fileInfoMapper.insert(f);
        return f;
    }

    /** Get Lock for fileInfo */
    private Lock getFileLock(String uuid) {
        return redisController.getLock(FILE_LOCK_PREFIX + uuid);
    }

}
