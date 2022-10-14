package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.UserInfoVo;
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
import com.yongj.util.IOUtils;
import com.yongj.util.PathUtils;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.util.AssertUtils.*;
import static com.curtisnewbie.common.util.ExceptionUtils.illegalState;
import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageableList;
import static com.yongj.enums.LockKeys.fileAccessKeySup;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {

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
    @Autowired
    private UserServiceFeign userServiceFeign;
    @Autowired
    private UserFileAccessMapper userFileAccessMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    @Transactional
    public void grantFileAccess(@NotNull GrantFileAccessCmd cmd) {
        final TUser tuser = cmd.getGrantedBy();

        // check if the grantedTo is the uploader
        AssertUtils.notEquals(cmd.getGrantedTo(), tuser.getUserId(), "You can't grant file access to yourself");

        final FileInfo file = fileInfoMapper.selectOne(MapperUtils.select(FileInfo::getId, FileInfo::getUploaderId, FileInfo::getFileType, FileInfo::getUuid)
                .eq(FileInfo::getId, cmd.getFileId())
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue()));
        nonNull(file, "File not found");

        // only uploader can grant access to the file
        AssertUtils.equals((int) file.getUploaderId(), tuser.getUserId(), "Only uploader can grant access to the file");
        // can only grant access to FILE not DIR
        AssertUtils.isTrue(!file.isDir(), "You can't not grant access to DIR (Directory) type files");

        LockUtils.lockAndRun(redisController.getLock(fileAccessKeySup.apply(file.getUuid())), () -> {

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

            // generate UserFileAccess to the file with GRANTED type
            _tryGenerateUserFileAccess(tuser.getUserNo(), file.getUuid(), FileAccessType.GRANTED);
        });
    }

    @Override
    public FileInfo uploadFile(UploadFileVo param) {
        final String uuid = UUID.randomUUID().toString();
        final int uploaderId = param.getUserId();

        final FsGroup fsGroup = writeFsGroupSupplier.supply(FsGroupType.USER);
        nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

        final String absPath = pathResolver.resolveAbsolutePath(uuid, uploaderId, fsGroup.getBaseFolder());
        nonNull(absPath, "Unable to resolve absolute path, unable to upload file, please contact administrator");

        long sizeInBytes;
        try {
            sizeInBytes = ioHandler.writeFile(absPath, param.getInputStream());
        } catch (IOException e) {
            log.error("Failed to write file, {}", absPath, e);
            throw new IllegalStateException("Failed to upload file, unknown error");
        }

        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(param.getFileName());
        f.setUploaderId(uploaderId);
        f.setUploaderName(param.getUsername());
        f.setUploadTime(LocalDateTime.now());
        f.setUuid(uuid);
        f.setUserGroup(param.getUserGroup().getValue());
        f.setSizeInBytes(sizeInBytes);
        f.setFsGroupId(fsGroup.getId());
        _doInsertFileInfo(f, param.getUserNo());
        return f;
    }

    @Override
    public FileInfo uploadFilesAsZip(final UploadZipFileVo param) throws IOException {
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
        _doInsertFileInfo(f, param.getUserNo());
        return f;
    }

    @Override
    public PageableList<FileInfoVo> listFilesByAccess(ListFileInfoReqVo reqVo) {
        SelectFileInfoListParam param = BeanCopyUtils.toType(reqVo, SelectFileInfoListParam.class);
        if (reqVo.filterForOwnedFilesOnly()) {
            param.setFilterOwnedFiles(true);
        }
        final Page<?> p = forPage(reqVo.getPagingVo());
        final List<FileInfoVo> dataList;
        final long count;

        /*
            Based on whether tagName is present, we use different queries

            Instead of using the Page<?> for pagination, we do COUNT(*) manually,
            the paginator plugin always fails to optimise the query :D
         */
        final long offset = p.getSize() * (p.getCurrent() - 1);
        final long limit = p.getSize();
        final boolean qryForTag = StringUtils.hasText(param.getTagName());
        if (qryForTag) {
            dataList = fileInfoMapper.selectFileListForUserAndTag(offset, limit, param);
            count = fileInfoMapper.countFileListForUserAndTag(param);
        } else {
            /*
                If parentFile is empty, and filename/userGroup are not searched, then we only return the top level file or dir.
                The query for tags will ignore parent_file param, so it's working fine
             */
            if (!StringUtils.hasText(param.getParentFile())
                    && !StringUtils.hasText(param.getFilename())
                    && param.getUserGroup() == null) {

                if (param.getFilename() != null) param.setFilename(null);
                param.setParentFile(""); // top-level file/dir
            }

            dataList = fileInfoMapper.selectFileListForUserSelective(offset, limit, param);
            count = fileInfoMapper.countFileListForUserSelective(param);
        }

        // set is_owner
        dataList.forEach(v -> v.checkAndSetIsOwner(reqVo.getUserId()));

        final PageableList<FileInfoVo> pl = new PageableList<>();
        pl.setPayload(dataList);
        pl.setPagingVo(PagingUtil.ofPageAndTotal((int) p.getCurrent(), count));
        return pl;

    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageableList<FileInfoVo> findPagedFilesForUser(@NotNull ListFileInfoReqVo reqVo) {
        SelectFileInfoListParam param = BeanCopyUtils.toType(reqVo, SelectFileInfoListParam.class);
        if (reqVo.filterForOwnedFilesOnly()) {
            param.setFilterOwnedFiles(true);
        }
        final Page<?> p = forPage(reqVo.getPagingVo());
        final List<FileInfoVo> dataList;
        final long count;

        /*
            Based on whether tagName is present, we use different queries

            Instead of using the Page<?> for pagination, we do COUNT(*) manually,
            the paginator plugin always fails to optimise the query :D
         */
        final long offset = p.getSize() * (p.getCurrent() - 1);
        final long limit = p.getSize();
        final boolean qryForTag = StringUtils.hasText(param.getTagName());
        if (qryForTag) {
            dataList = fileInfoMapper.selectFileListForUserAndTag(offset, limit, param);
            count = fileInfoMapper.countFileListForUserAndTag(param);
        } else {
            /*
                If parentFile is empty, and filename/userGroup are not searched, then we only return the top level file or dir.
                The query for tags will ignore parent_file param, so it's working fine
             */
            if (!StringUtils.hasText(param.getParentFile())
                    && !StringUtils.hasText(param.getFilename())
                    && param.getUserGroup() == null) {

                if (param.getFilename() != null) param.setFilename(null);
                param.setParentFile(""); // top-level file/dir
            }

            dataList = fileInfoMapper.selectFileListForUserSelective(offset, limit, param);
            count = fileInfoMapper.countFileListForUserSelective(param);
        }

        // set is_owner
        dataList.forEach(v -> v.checkAndSetIsOwner(reqVo.getUserId()));

        final PageableList<FileInfoVo> pl = new PageableList<>();
        pl.setPayload(dataList);
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
    public List<FileUploaderInfoVo> findFilesWithoutUploaderName(int limit) {
        final Wrapper<FileInfo> cond = MapperUtils.select(FileInfo::getId, FileInfo::getUploaderId)
                .eq(FileInfo::getUploaderName, "");

        return fileInfoMapper.selectListAndConvert(cond, (f) -> FileUploaderInfoVo.builder()
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
        final FileDownloadValidInfo f = fileInfoMapper.selectValidateInfoById(fileId, userNo);
        nonNull(f, "File is not found");
        isFalse(f.isDeleted(), "File is deleted");
        isTrue(f.isNotDir(), "Downloading a directory is not supported");

        if (f.isPublicGroup()) return;

        // current user is the uploader
        if (Objects.equals(f.getUploaderId(), userId)) return;

        // file shared by the uploader
        if (f.getAccessType() != null) return;

        // file belongs to a folder that current user has access to
        if (fileInfoMapper.selectAnyUserFolderIdForFile(fileId, userNo) != null) return;

        throw new UnrecoverableException("You are not allowed to download this file");
    }

    @Override
    public void moveFileInto(int userId, String uuid, String parentFileUuid) {
        LockUtils.lockAndRun(getFileLock(uuid), () -> {

            final FileInfo f = fileInfoMapper.selectOne(Wrappers.lambdaQuery(FileInfo.class)
                    .eq(FileInfo::getUuid, uuid)
                    .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL));
            nonNull(f, "Record not found");
            isFalse(f.isDir(), "Directory can't be moved into another directory");
            AssertUtils.equals(userId, (int) f.getUploaderId(), "Only the uploader can move files");

            // if parentFileUuid is empty, we just try to move it out of the directory
            if (!StringUtils.hasText(parentFileUuid)) {
                if (StringUtils.hasText(f.getParentFile())) {
                    FileInfo update = new FileInfo();
                    update.setId(f.getId());
                    update.setParentFile("");
                    fileInfoMapper.updateById(update);
                }
                return;
            }

            // move into directory
            LockUtils.lockAndRun(getFileLock(parentFileUuid), () -> {
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
        final FileInfo fileInfo = fileInfoMapper.selectById(cmd.getId());
        nonNull(fileInfo, "Record not found");
        AssertUtils.equals((int) fileInfo.getUploaderId(), cmd.getUpdatedById(), "You are not allowed to update this file");

        // Directory is by default private, and it's not allowed to update it
        if (fileInfo.isDir()
                && cmd.getUserGroup() != null
                && cmd.getUserGroup().getValue() != (int) fileInfo.getUserGroup()) {
            throw new UnrecoverableException("Updating directory's UserGroup is not allowed");
        }

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
    public PageableList<FileSharingVo> listGrantedAccess(int fileId, int requestUserId, @NotNull Page page) {
        Assert.isTrue(isFileOwner(requestUserId, fileId), "Only uploader can list granted access");

        return fileSharingMapper.selectPageAndConvert(
                MapperUtils.select(FileSharing::getId, FileSharing::getUserId, FileSharing::getCreateTime, FileSharing::getCreateBy)
                        .eq(FileSharing::getFileId, fileId)
                        .eq(FileSharing::getIsDel, IsDel.NORMAL)
                        .orderByDesc(FileSharing::getId), page, fileSharingConverter::toVo);
    }

    @Override
    public void removeGrantedAccess(int fileId, int userId, int removedByUserId) {

        // we don't use FileInfo inside the lock, so it's fine to fetch it beforehand
        final FileInfo f = fileInfoMapper.selectOne(MapperUtils.select(FileInfo::getId, FileInfo::getUuid)
                .eq(FileInfo::getId, fileId)
                .eq(FileInfo::getUploaderId, removedByUserId)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue()));
        Assert.isTrue(f != null, "Only uploader can remove granted access");
        final String fileKey = f.getUuid();

        // TODO optimise this?
        final String userNo = Objects.requireNonNull(fetchUserNo(userId));

        LockUtils.lockAndRun(redisController.getLock(fileAccessKeySup.apply(fileKey)), () -> {
            // delete fileSharing logically
            fileSharingMapper.update(MapperUtils.set(FileSharing::getIsDel, IsDel.DELETED)
                    .eq(FileSharing::getFileId, fileId)
                    .eq(FileSharing::getUserId, userId)
                    .eq(FileSharing::getIsDel, IsDel.NORMAL));

            // delete fileAccess physically
            _delUserFileAccess(userNo, fileKey);
        });
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
        _doInsertFileInfo(dir, r.getUserNo());
        return dir;
    }

    @Override
    public List<ListDirVo> listDirs(int userId) {
        final List<FileInfo> fileInfos = fileInfoMapper.selectList(Wrappers.lambdaQuery(FileInfo.class)
                .select(FileInfo::getId, FileInfo::getUuid, FileInfo::getName)
                .eq(FileInfo::getUploaderId, userId)
                .eq(FileInfo::getFileType, FileType.DIR)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                .eq(FileInfo::getIsDel, IsDel.NORMAL));
        return BeanCopyUtils.toTypeList(fileInfos, ListDirVo.class);
    }

    @Override
    public List<String> listFilesInDir(String fileKey, long limit, long offset) {
        final SFunction<FileInfo, String> select = FileInfo::getUuid;
        return fileInfoMapper.selectListAndConvert(Wrappers.lambdaQuery(FileInfo.class)
                .select(select)
                .eq(FileInfo::getParentFile, fileKey)
                .eq(FileInfo::getFileType, FileType.FILE)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                .eq(FileInfo::getIsDel, IsDel.NORMAL)
                .last(PagingUtil.limit(offset, limit)), select);
    }

    @Override
    public boolean filenameExists(String filename, int userId) {
        return fileInfoMapper.selectOne(Wrappers.lambdaQuery(FileInfo.class)
                .select(FileInfo::getId)
                .eq(FileInfo::getName, filename)
                .eq(FileInfo::getUploaderId, userId)
                .eq(FileInfo::getFileType, FileType.FILE)
                .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                .eq(FileInfo::getIsDel, IsDel.NORMAL)
                .last("limit 1")) != null;
    }

    @Override
    public void exportAsZip(ExportAsZipReq r, TUser user) {
        // lock is to prevent multiple export
        final RLock lock = (RLock) redisController.getLock("fs:export:zip:" + user.getUserNo());
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(1, -1, TimeUnit.MILLISECONDS);
            if (!isLocked)
                return;

            final String filePre = "export_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            final String folderName = user.getUserId() + "_" + filePre;
            final String zipFileName = filePre + ".zip";

            // temp dir
            final String tmpPre = "/tmp/";
            final String tmpDirPath = tmpPre + folderName;
            final File tmpDir = new File(tmpDirPath);
            tmpDir.mkdir();

            // fsgroup
            final FsGroup fsGroup = writeFsGroupSupplier.supply(FsGroupType.USER);
            nonNull(fsGroup, "No writable fs_group found, unable to export, userNo: {}", user.getUserNo());

            // copy all these files to temp dir, where we compress them as a zip
            final Set<String> fnames = new HashSet<>(); // to avoid name collision
            final List<File> entries = r.getFileIds().stream()
                    .map(fid -> {
                        try {
                            validateUserDownload(user.getUserId(), fid, user.getUserNo());

                            // to avoid name collision, if we found a file with the same name, we add suffix to it
                            String fname = PathUtils.escapeFilename(findById(fid).getName());
                            while (!fnames.add(fname)) {
                                fname = PathUtils.getNextFilename(fname);
                            }

                            // copy the file to the tmp dir
                            final File toFile = new File(tmpDirPath + "/" + fname);
                            IOUtils.copy(retrieveFileChannel(fid), toFile);

                            return toFile;
                        } catch (Exception e) {
                            log.warn("Failed to copy file to temp dir for exporting, id: {}, tmpDirPath: {}", fid, tmpDirPath, e);
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            // resolve a abs path for the zip file
            final String uuid = UUID.randomUUID().toString();
            final String absPath = pathResolver.resolveAbsolutePath(uuid, user.getUserId(), fsGroup.getBaseFolder());
            nonNull(absPath, "Unable to resolve absolute path, unable to upload file");

            // do compression
            long size = -1;
            try {
                size = ioHandler.writeLocalZipFile(absPath, entries);
            } catch (IOException e) {
                log.error("Failed to compress zip files for exporting, userNo: {}", user.getUserNo(), e);
            }

            // TODO needs refactoring
            // if compression was successful
            if (size > -1) {
                FileInfo f = new FileInfo();
                f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
                f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
                f.setName(zipFileName);
                f.setUploaderId(user.getUserId());
                f.setUploaderName(user.getUsername());
                f.setUploadTime(LocalDateTime.now());
                f.setUuid(uuid);
                f.setUserGroup(FileUserGroupEnum.PRIVATE.getValue());
                f.setSizeInBytes(size);
                f.setFsGroupId(fsGroup.getId());
                _doInsertFileInfo(f, user.getUserNo());
            }
        } catch (Exception e) {
            log.info("exportAsZip threw exception, userNo: {}", user.getUserNo(), e);
        } finally {
            if (isLocked) lock.unlock();
        }
    }

    @Override
    public void loadUserFileAccess() {
        final Lock lock = redisController.getLock("file:user:access:refresh:global");
        LockUtils.lockAndRun(lock, () -> {

            Paginator<FileInfo> paginator = new Paginator<>(p ->
                    fileInfoMapper.selectList(
                            MapperUtils.select(FileInfo::getId, FileInfo::getUuid, FileInfo::getUploaderId)
                                    .eq(FileInfo::getIsLogicDeleted, FileLogicDeletedEnum.NORMAL.getValue())
                                    .orderByAsc(FileInfo::getId) // make the old ones are generated first
                                    .last(PagingUtil.limit(p.getOffset(), p.getLimit()))))
                    .pageSize(100);

            paginator.loopEachTilEnd(fi -> {
                final String fileKey = fi.getUuid();
                Runner.runSafely(() -> {
                    LockUtils.lockAndRun(redisController.getLock(fileAccessKeySup.apply(fileKey)), () -> {
                        // create access for the uploader of the user
                        _tryGenerateUserFileAccess(fetchUserNo(fi.getUploaderId()), fileKey, FileAccessType.OWNER);

                        // create access for the shared access
                        fileSharingMapper.selectList(MapperUtils.eq(FileSharing::getFileId, fi.getId()))
                                .forEach(fs -> _tryGenerateUserFileAccess(fetchUserNo(fs.getUserId()), fileKey, FileAccessType.GRANTED));
                    });
                }, (e) -> log.error("Failed to load user file access, fileKey: {}", fileKey));
            });
        });

    }

    // ------------------------------------- private helper methods ------------------------------------

    /** Insert FileInfo and create UserFileAccess */
    private void _doInsertFileInfo(FileInfo f, String userNo) {
        transactionTemplate.execute((tx) -> {
            fileInfoMapper.insert(f);
            _tryGenerateUserFileAccess(userNo, f.getUuid(), FileAccessType.OWNER);
            return null;
        });
    }

    private String fetchUserNo(int userId) {
        final UserInfoVo user = Result.tryGetData(userServiceFeign.fetchUserInfo(userId),
                () -> String.format("UserServiceFeign#fetchUserInfo, userId: %d", userId));
        Assert.notNull(user, "userInfoVo == null");
        return user.getUserNo();
    }

    /**
     * delete userFileAccess, this method doesn't have lock internally, but it needs one
     * <p>
     * see {@link LockKeys#fileAccessKeySup}
     */
    private void _delUserFileAccess(String userNo, String fileKey) {
        userFileAccessMapper.delete(MapperUtils.eq(UserFileAccess::getUserNo, userNo)
                .eq(UserFileAccess::getFileUuid, fileKey));
    }

    /**
     * try generate userFileAccess, this method doesn't have lock internally, but it needs one
     * <p>
     * see {@link LockKeys#fileAccessKeySup}
     */
    private void _tryGenerateUserFileAccess(String userNo, String fileKey, FileAccessType fat) {
        UserFileAccess ufa = userFileAccessMapper.selectOne(MapperUtils.eq(UserFileAccess::getUserNo, userNo)
                .eq(UserFileAccess::getFileUuid, fileKey));
        if (ufa != null) return;

        ufa = new UserFileAccess();
        ufa.setUserNo(userNo);
        ufa.setFileUuid(fileKey);
        ufa.setAccessType(fat);
        userFileAccessMapper.insert(ufa);
    }

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

    /** Get Lock for fileInfo */
    private Lock getFileLock(String uuid) {
        return redisController.getLock(LockKeys.fileKeySup.apply(uuid));
    }

}