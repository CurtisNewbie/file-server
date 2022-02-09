package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
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
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileSharingIsDel;
import com.yongj.enums.FileUserGroupEnum;
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

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.Lock;

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
        // make sure the file exists
        // check if the file exists
        QueryWrapper<FileInfo> fQry = new QueryWrapper<>();
        fQry.select("id", "uploader_id")
                .eq("id", cmd.getFileId())
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue());
        FileInfo file = fileInfoMapper.selectOne(fQry);
        AssertUtils.nonNull(file, "File not found");

        // check if the grantedTo is the uploader
        AssertUtils.notEquals(file.getUploaderId(), cmd.getGrantedTo(), "You can't grant access to the file's uploader");

        // check if the user already had access to the file
        QueryWrapper<FileSharing> fsQry = new QueryWrapper<>();
        fsQry.select("id", "is_del")
                .eq("file_id", cmd.getFileId())
                .eq("user_id", cmd.getGrantedTo());
        FileSharing fileSharing = fileSharingMapper.selectOne(fsQry);
        AssertUtils.isTrue(fileSharing == null || Objects.equals(fileSharing.getIsDel(), FileSharingIsDel.TRUE.getValue()),
                "User already had access to this file");

        if (fileSharing == null) {
            // insert file_sharing record
            final LocalDateTime now = LocalDateTime.now();
            fileSharingMapper.insert(FileSharing.builder()
                    .userId(cmd.getGrantedTo())
                    .fileId(cmd.getFileId())
                    .createdBy(cmd.getGrantedBy())
                    .createDate(now)
                    .updatedBy(cmd.getGrantedBy())
                    .updateDate(now)
                    .build());
        } else {
            // update is_del to false
            FileSharing updateParam = new FileSharing();
            updateParam.setId(fileSharing.getId());
            updateParam.setIsDel(FileSharingIsDel.FALSE.getValue());
            updateParam.setUpdateDate(LocalDateTime.now());
            updateParam.setUpdatedBy(cmd.getGrantedBy());
            fileSharingMapper.updateById(updateParam);
        }
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
        AssertUtils.nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

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
        f.setUuid(uuid);
        f.setUserGroup(userGroup.getValue());
        f.setSizeInBytes(sizeInBytes);
        f.setFsGroupId(fsGroup.getId());
        fileInfoMapper.insert(f);
        return f;
    }

    @Override
    public FileInfo uploadFilesAsZip(final UploadZipFileVo param) throws IOException {
        final int userId = param.getUserId();
        final String zipFile = param.getZipFile();
        final String[] entryNames = param.getEntryNames();
        final FileUserGroupEnum userGroup = param.getUserGroup();
        final InputStream[] inputStreams = param.getInputStreams();

        AssertUtils.notEmpty(entryNames);
        AssertUtils.nonNull(userGroup);
        AssertUtils.hasText(zipFile);
        AssertUtils.notEmpty(inputStreams);
        AssertUtils.equals(entryNames.length, inputStreams.length);

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();

        // find the first writable fs_group to use
        FsGroup fsGroup = fsGroupService.findFirstFsGroupForWrite();
        AssertUtils.nonNull(fsGroup, "No writable fs_group found, unable to upload file, please contact administrator");

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, userId, fsGroup.getBaseFolder());
        // create directories if not exists
        ioHandler.createParentDirIfNotExists(absPath);
        // write file to channel
        final long sizeInBytes = ioHandler.writeZipFile(absPath, prepareZipEntries(entryNames, inputStreams));
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
        AssertUtils.nonNull(fi, "Record not found");

        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        AssertUtils.nonNull(fi, "Unable to download file, fs_group for this file is not found");

        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        ioHandler.readFile(absPath, outputStream);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FileInfo findById(int id) {
        return fileInfoMapper.selectById(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public InputStream retrieveFileInputStream(int id) throws IOException {
        FileInfo fi = fileInfoMapper.selectDownloadInfoById(id);
        AssertUtils.nonNull(fi, "Record not found");

        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        AssertUtils.nonNull(fsg, "FS Group for this record is not found");

        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        return Files.newInputStream(Paths.get(absPath));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void validateUserDownload(int userId, int id) {
        // validate whether this file can be downloaded by current user
        FileInfo f = fileInfoMapper.selectValidateInfoById(id, userId);
        AssertUtils.nonNull(f, "File is not found or you are not allowed to download this file");
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getFilename(int id) {
        return fileInfoMapper.selectNameById(id);
    }

    @Override
    public void deleteFileLogically(int userId, int id) {
        // check if the file is owned by this user
        Integer uploaderId = fileInfoMapper.selectUploaderIdById(id);
        AssertUtils.nonNull(uploaderId, "Record not found");
        AssertUtils.equals(userId, (int) uploaderId, "You can only delete file that you uploaded");
        fileInfoMapper.logicDelete(id);
    }

    @Override
    public void markFileDeletedPhysically(int id) {
        fileInfoMapper.markFilePhysicDeleted(id, new Date());
    }

    @Override
    public void updateFileUserGroup(int id, @NotNull FileUserGroupEnum fug, int userId)
            throws MsgEmbeddedException {
        Integer uploader = fileInfoMapper.selectUploaderIdById(id);
        if (uploader == null)
            throw new MsgEmbeddedException("File not found");

        if (!Objects.equals(uploader, userId))
            throw new MsgEmbeddedException("You are not allowed to update this file");

        fileInfoMapper.updateFileUserGroup(id, fug.getValue());
    }

    @Override
    public void updateFile(@NotNull UpdateFileCmd cmd) {
        FileInfo fi = new FileInfo();
        fi.setId(cmd.getId());
        fi.setUserGroup(cmd.getUserGroup().getValue());
        fi.setName(cmd.getFileName());
        fileInfoMapper.updateInfo(fi);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<FileSharingVo>> listGrantedAccess(int fileId, @NotNull PagingVo paging) {
        QueryWrapper<FileSharing> condition = new QueryWrapper<>();
        condition.select("id", "user_id", "create_date", "created_by")
                .eq("file_id", fileId)
                .eq("is_del", FileSharingIsDel.FALSE.getValue())
                .orderBy(true, false, "id");
        Page page = PagingUtil.forPage(paging);
        return PagingUtil.toPageList(fileSharingMapper.selectPage(page, condition), fileSharingConverter::toVo);
    }

    @Override
    public void removeGrantedAccess(int fileId, int userId, int removedBy) {
        FileSharing updateParam = new FileSharing();
        updateParam.setIsDel(FileSharingIsDel.TRUE.getValue());
        updateParam.setUpdatedBy(String.valueOf(removedBy));
        updateParam.setUpdateDate(LocalDateTime.now());

        QueryWrapper<FileSharing> whereCondition = new QueryWrapper<>();
        whereCondition
                .eq("file_id", fileId)
                .eq("user_id", userId)
                .eq("is_del", FileSharingIsDel.FALSE.getValue());
        fileSharingMapper.update(updateParam, whereCondition);
    }

    @Override
    public void updateUploaderName(int fileId, @NotNull String uploaderName) {
        final FileInfo updateParam = new FileInfo();
        updateParam.setUploaderName(uploaderName);

        final QueryWrapper<FileInfo> cond = new QueryWrapper<FileInfo>()
                .eq("id", fileId)
                .eq("uploader_name", ""); // make sure we don't accidentally overwrite previous name

        fileInfoMapper.update(updateParam, cond);
    }

    @Override
    public void tagFile(final TagFileCmd cmd) {
        final String tagName = cmd.getTagName();
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

    // ------------------------------------- private helper methods ------------------------------------

    private List<ZipCompressEntry> prepareZipEntries(String[] entryNames, InputStream[] entries) {
        List<ZipCompressEntry> l = new ArrayList<>(entries.length);
        for (int i = 0; i < entries.length; i++)
            l.add(new ZipCompressEntry(entryNames[i], entries[i]));
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

}
