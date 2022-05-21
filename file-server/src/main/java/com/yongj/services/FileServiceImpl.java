package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PageableVo;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.converters.FileInfoConverter;
import com.yongj.converters.FileSharingConverter;
import com.yongj.converters.TagConverter;
import com.yongj.dao.*;
import com.yongj.enums.*;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.io.ZipCompressEntry;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import static com.curtisnewbie.common.util.AssertUtils.*;
import static com.curtisnewbie.common.util.PagingUtil.forPage;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
@Transactional
public class FileServiceImpl implements FileService {

    @Autowired
    private FileInfoConverter fileInfoConverter;
    @Autowired
    private FileInfoMapper fileInfoMapper;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private FsGroupService fsGroupService;
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

    @Override
    public void grantFileAccess(@NotNull GrantFileAccessCmd cmd) {
        // check if the grantedTo is the uploader
        AssertUtils.notEquals(cmd.getGrantedTo(), cmd.getGrantedByUserId(), "You can't grant file access to yourself");

        // make sure the file exists
        // check if the file exists
        QueryWrapper<FileInfo> fQry = new QueryWrapper<>();
        fQry.select("id", "uploader_id")
                .eq("id", cmd.getFileId())
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue());
        FileInfo file = fileInfoMapper.selectOne(fQry);
        nonNull(file, "File not found");

        // only uploader can grant access to the file
        AssertUtils.equals((int) file.getUploaderId(), cmd.getGrantedByUserId(), "Only uploader can grant access to the file");

        // check if the user already had access to the file
        QueryWrapper<FileSharing> fsQry = new QueryWrapper<>();
        fsQry.select("id", "is_del")
                .eq("file_id", cmd.getFileId())
                .eq("user_id", cmd.getGrantedTo());
        FileSharing fileSharing = fileSharingMapper.selectOne(fsQry);
        isTrue(fileSharing == null || Objects.equals(fileSharing.getIsDel(), FileSharingIsDel.TRUE.getValue()),
                "User already had access to this file");

        if (fileSharing == null) {
            // insert file_sharing record
            final LocalDateTime now = LocalDateTime.now();
            FileSharing fs = new FileSharing();
            fs.setUserId(cmd.getGrantedTo());
            fs.setFileId(cmd.getFileId());
            fs.setCreateBy(cmd.getGrantedByName());
            fs.setUpdateBy(cmd.getGrantedByName());
            fileSharingMapper.insert(fs);
        } else {
            // update is_del to false
            FileSharing updateParam = new FileSharing();
            updateParam.setId(fileSharing.getId());
            updateParam.setIsDel(IsDel.NORMAL);
            updateParam.setUpdateBy(cmd.getGrantedByName());
            fileSharingMapper.updateById(updateParam);
        }
    }

    @Override
    public FileInfo uploadAppFile(@NotNull UploadAppFileCmd cmd) throws IOException {
        final String fileName = cmd.getFileName();
        final String uploadApp = cmd.getUploadApp();
        final InputStream inputStream = cmd.getInputStream();

        hasText(fileName, "fileName is empty");
        hasText(uploadApp, "uploadApp is empty");
        notNull(inputStream, "inputStream == null");

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();

        // find the first writable fs_group to use
        FsGroup fsGroup = fsGroupService.findFirstFsGroupForWrite();
        nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, uploadApp, fsGroup.getBaseFolder());

        // create directories if not exists
        ioHandler.createParentDirIfNotExists(absPath);

        // write file to channel
        final long sizeInBytes = ioHandler.writeFile(absPath, inputStream);

        // save file info record
        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(fileName);
        f.setUploadTime(LocalDateTime.now());
        f.setUuid(uuid);
        f.setUserGroup(FileUserGroupEnum.PRIVATE.getValue()); // always private, but it's not displayed anyway
        f.setSizeInBytes(sizeInBytes);
        f.setUploadType(UploadType.APP_UPLOADED); // uploaded by another app
        f.setUploadApp(uploadApp); // app that uploaded this
        f.setFsGroupId(fsGroup.getId());
        f.setCreateBy(uploadApp);
        f.setCreateTime(LocalDateTime.now());
        fileInfoMapper.insert(f);
        return f;
    }

    @Override
    public FileInfo uploadFile(@NotNull UploadFileVo param) throws IOException {
        final String fileName = param.getFileName();
        final FileUserGroupEnum userGroup = param.getUserGroup();
        final InputStream inputStream = param.getInputStream();
        final int uploaderId = param.getUserId();

        Assert.notNull(fileName, "fileName == null");
        Assert.notNull(userGroup, "userGroup == null");
        Assert.notNull(inputStream, "inputStream == null");

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();
        // find the first writable fs_group to use
        FsGroup fsGroup = fsGroupService.findFirstFsGroupForWrite();
        nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, uploaderId, fsGroup.getBaseFolder());
        // create directories if not exists
        ioHandler.createParentDirIfNotExists(absPath);
        // write file to channel
        final long sizeInBytes = ioHandler.writeFile(absPath, inputStream);
        // save file info record
        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(fileName);
        f.setUploaderId(uploaderId);
        f.setUploaderName(param.getUsername());
        f.setUploadTime(LocalDateTime.now());
        f.setUploadType(UploadType.USER_UPLOADED);
        f.setUuid(uuid);
        f.setUserGroup(userGroup.getValue());
        f.setSizeInBytes(sizeInBytes);
        f.setFsGroupId(fsGroup.getId());
        f.setCreateBy(param.getUsername());
        f.setCreateTime(LocalDateTime.now());
        fileInfoMapper.insert(f);
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

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();

        // find the first writable fs_group to use
        FsGroup fsGroup = fsGroupService.findFirstFsGroupForWrite();
        nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, userId, fsGroup.getBaseFolder());
        // create directories if not exists
        ioHandler.createParentDirIfNotExists(absPath);
        // write file to channel
        final long sizeInBytes = ioHandler.writeZipFile(absPath, prepareZipEntries(multipartFiles));
        // save file info record
        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(zipFile.endsWith(".zip") ? zipFile : zipFile + ".zip");
        f.setUploaderId(userId);
        f.setUploadTime(LocalDateTime.now());
        f.setUploaderName(param.getUsername());
        f.setUploadType(UploadType.USER_UPLOADED);
        f.setUuid(uuid);
        f.setUserGroup(userGroup.getValue());
        f.setSizeInBytes(sizeInBytes);
        f.setFsGroupId(fsGroup.getId());
        f.setCreateBy(param.getUsername());
        f.setCreateTime(LocalDateTime.now());
        fileInfoMapper.insert(f);
        return f;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<FileInfoVo>> findPagedFilesForUser(@NotNull ListFileInfoReqVo reqVo) {
        SelectFileInfoListParam param = BeanCopyUtils.toType(reqVo, SelectFileInfoListParam.class);
        if (reqVo.filterForOwnedFilesOnly()) {
            param.setFilterOwnedFiles(true);
        }
        final Page<?> p = forPage(reqVo.getPagingVo());
        // based on whether tagName is present, we use different queries
        IPage<FileInfo> dataList = StringUtils.hasText(param.getTagName()) ?
                fileInfoMapper.selectFileListForUserAndTag(p, param.getUserId(), param.getTagName(), param.getFilename()) :
                fileInfoMapper.selectFileListForUserSelective(p, param);
        return PagingUtil.toPageList(dataList, (e) -> {
            FileInfoVo v = fileInfoConverter.toVo(e);
            v.setIsOwner(Objects.equals(e.getUploaderId(), reqVo.getUserId()));
            return v;
        });
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<PhysicDeleteFileVo>> findPagedFileIdsForPhysicalDeleting(@NotNull PagingVo pagingVo) {
        IPage<FileInfo> dataList = fileInfoMapper.findInfoForPhysicalDeleting(forPage(pagingVo));
        return PagingUtil.toPageList(dataList, fileInfoConverter::toPhysicDeleteFileVo);
    }

    @Override
    public PageablePayloadSingleton<List<FileUploaderInfoVo>> findPagedFilesWithoutUploaderName(@NotNull PagingVo pagingVo) {
        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .select("id", "uploader_id")
                .eq("uploader_name", "");

        final IPage<FileInfo> dataList = fileInfoMapper.selectPage(forPage(pagingVo), cond);
        return PagingUtil.toPageList(dataList, (f) ->
                FileUploaderInfoVo.builder()
                        .id(f.getId())
                        .uploaderId(f.getUploaderId())
                        .build());
    }

    @Override
    public void downloadFile(int id, @NotNull OutputStream outputStream) throws IOException {
        FileInfo fi = fileInfoMapper.selectById(id);
        nonNull(fi, "Record not found");

        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        nonNull(fi, "Unable to download file, fs_group for this file is not found");

        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        ioHandler.readFile(absPath, outputStream);
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
    public void validateAppDownload(@NotBlank String appName, int fileId) {
        FileInfo fi = fileInfoMapper.selectById(fileId);
        nonNull(fi, "File not found");

        isFalse(fi.isDeleted(), "File is deleted");
        isTrue(fi.getUploadType() == UploadType.APP_UPLOADED, "Incorrect UploadType, not permitted");
        AssertUtils.equals(appName, fi.getUploadApp(), "App name does not match, not permitted");
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void validateUserDownload(int userId, int id) {
        // validate whether this file can be downloaded by current user
        FileInfo f = fileInfoMapper.selectValidateInfoById(id, userId);
        nonNull(f, "File is not found or you are not allowed to download this file");
    }

    @Override
    public void deleteFileLogically(int userId, int id) {
        // check if the file is owned by this user
        Integer uploaderId = fileInfoMapper.selectUploaderIdById(id);
        nonNull(uploaderId, "Record not found");
        AssertUtils.equals(userId, (int) uploaderId, "You can only delete file that you uploaded");
        fileInfoMapper.logicDelete(id);
    }

    @Override
    public void markFileDeletedPhysically(int id) {
        fileInfoMapper.markFilePhysicDeleted(id, LocalDateTime.now());
    }

    @Override
    public void updateFileUserGroup(int id, @NotNull FileUserGroupEnum fug, int userId, @Nullable String updatedBy) {
        Integer uploader = fileInfoMapper.selectUploaderIdById(id);
        nonNull(uploader, "File not found");
        AssertUtils.equals((int) uploader, userId, "You are not allowed to update this file");

        final FileInfo param = new FileInfo();
        param.setUpdateBy(updatedBy);
        param.setUpdateTime(LocalDateTime.now());
        param.setUserGroup(fug.getValue());

        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .eq("id", id)
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue())
                .eq("is_del", IsDel.NORMAL.getValue());

        fileInfoMapper.update(param, cond);
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
        fi.setUpdateTime(LocalDateTime.now());
        fi.setUpdateBy(cmd.getUpdatedByName());

        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .eq("id", cmd.getId())
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue())
                .eq("is_del", IsDel.NORMAL.getValue());

        fileInfoMapper.update(fi, cond);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<FileSharingVo>> listGrantedAccess(int fileId, int requestUserId, @NotNull PagingVo paging) {
        Assert.isTrue(isFileOwner(requestUserId, fileId), "Only uploader can list granted access");

        LambdaQueryWrapper<FileSharing> condition = new LambdaQueryWrapper<>();
        condition.select(FileSharing::getId, FileSharing::getUserId, FileSharing::getCreateTime, FileSharing::getCreateBy)
                .eq(FileSharing::getFileId, fileId)
                .eq(FileSharing::getIsDel, IsDel.NORMAL)
                .orderByDesc(FileSharing::getId);
        Page page = PagingUtil.forPage(paging);
        return PagingUtil.toPageList(fileSharingMapper.selectPage(page, condition), fileSharingConverter::toVo);
    }

    @Override
    public void removeGrantedAccess(int fileId, int userId, int removedByUserId) {
        Assert.isTrue(isFileOwner(removedByUserId, fileId), "Only uploader can remove granted access");

        FileSharing updateParam = new FileSharing();
        updateParam.setIsDel(IsDel.DELETED);
        updateParam.setUpdateBy(String.valueOf(removedByUserId));

        QueryWrapper<FileSharing> whereCondition = new QueryWrapper<>();
        whereCondition
                .eq("file_id", fileId)
                .eq("user_id", userId)
                .eq("is_del", FileSharingIsDel.FALSE.getValue());
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
        final String taggedBy = cmd.getTaggedBy();
        final int fileId = cmd.getFileId();
        final int userId = cmd.getUserId();

        // tag the file with lock
        final Lock lock = getFileTagLock(userId, fileId, tagName);
        try {
            lock.lock();

            // find the tag first, and create one for current user if necessary
            final int tagId = createTagIfNecessary(userId, tagName, cmd.getTaggedBy());

            // check if it's already tagged
            final FileTag selected = selectFileTag(fileId, tagId);

            // insert one if it doesn't exist
            if (selected == null) {
                FileTag inserted = new FileTag();
                inserted.setUserId(userId);
                inserted.setFileId(fileId);
                inserted.setTagId(tagId);
                inserted.setCreateBy(taggedBy);
                inserted.setCreateTime(LocalDateTime.now());
                fileTagMapper.insert(inserted);
                return;
            }

            // reset, if it's deleted
            if (selected.isDeleted()) {
                final FileTag updated = new FileTag();
                updated.setIsDel(IsDel.NORMAL);
                updated.setUpdateBy(taggedBy);
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
        final String untaggedBy = cmd.getUntaggedBy();
        final int fileId = cmd.getFileId();
        final int userId = cmd.getUserId();

        final Lock lock = getFileTagLock(userId, fileId, tagName);
        try {
            lock.lock();

            Tag tag = selectTag(userId, tagName);
            if (tag == null) {
                log.info("Tag for '{}' doesn't exist, unable to untag file", tagName);
                return;
            }

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
                updated.setUpdateBy(untaggedBy);
                final QueryWrapper<FileTag> where = new QueryWrapper<FileTag>()
                        .eq("id", fileTag.getId())
                        .eq("is_del", IsDel.NORMAL.getValue());
                if (fileTagMapper.update(updated, where) > 0)
                    log.info("Untagged file, file_id: {}, tag_name: {}", fileId, tagName);
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
    public PageableVo<List<TagVo>> listFileTags(final int userId, final int fileId, final Page<?> page) {
        return PagingUtil.toPageable(fileTagMapper.listTagsForFile(page, userId, fileId), tagConverter::toVo);
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
    private int createTagIfNecessary(final int userId, final String tagName, final String createBy) {
        final Tag selected = selectTag(userId, tagName);
        if (selected == null) {
            Tag inserted = new Tag();
            inserted.setUserId(userId);
            inserted.setName(tagName);
            inserted.setCreateBy(createBy);
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

    private Lock getFileTagLock(int userId, int fileId, String tagName) {
        return redisController.getLock(String.format("file:tag:uid:%s:fid:%s:name:%s", userId, fileId, tagName));
    }

    private Path resolveFilePath(int id) {
        FileInfo fi = fileInfoMapper.selectDownloadInfoById(id);
        nonNull(fi, "Record not found");

        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        nonNull(fsg, "FS Group for this record is not found");

        final String absPath;
        if (fi.getUploadType() == UploadType.APP_UPLOADED)
            absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploadApp(), fsg.getBaseFolder());
        else
            absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        return Paths.get(absPath);
    }

}
