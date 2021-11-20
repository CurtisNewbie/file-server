package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.converters.FileInfoConverter;
import com.yongj.converters.FileSharingConverter;
import com.yongj.dao.*;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileSharingIsDel;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.NoWritableFsGroupException;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.io.ZipCompressEntry;
import com.yongj.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static com.curtisnewbie.common.util.PagingUtil.forPage;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class FileInfoServiceImpl implements FileInfoService {

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

    @Override
    public void grantFileAccess(@NotNull GrantFileAccessCmd cmd) throws MsgEmbeddedException {
        // make sure the file exists
        // check if the file exists
        QueryWrapper<FileInfo> fQry = new QueryWrapper<>();
        fQry.select("id", "uploader_id")
                .eq("id", cmd.getFileId())
                .eq("is_logic_deleted", FileLogicDeletedEnum.NORMAL.getValue());
        FileInfo file = fileInfoMapper.selectOne(fQry);
        ValidUtils.requireNonNull(file, "File not found");

        // check if the grantedTo is the uploader
        ValidUtils.requireNotEquals(file.getUploaderId(), cmd.getGrantedTo(), "You can't grant access to the file's uploader");

        // check if the user already had access to the file
        QueryWrapper<FileSharing> fsQry = new QueryWrapper<>();
        fsQry.select("id", "is_del")
                .eq("file_id", cmd.getFileId())
                .eq("user_id", cmd.getGrantedTo());
        FileSharing fileSharing = fileSharingMapper.selectOne(fsQry);
        ValidUtils.assertTrue(fileSharing == null || Objects.equals(fileSharing.getIsDel(), FileSharingIsDel.TRUE.getValue()),
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
    public FileInfo uploadFile(int userId, String fileName, FileUserGroupEnum userGroup, InputStream inputStream) throws IOException {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(userGroup);
        Objects.requireNonNull(inputStream);

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();
        // find the first writable fs_group to use
        FsGroup fsGroup = fsGroupService.findFirstFsGroupForWrite();
        if (fsGroup == null)
            throw new NoWritableFsGroupException();

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, userId, fsGroup.getBaseFolder());
        // create directories if not exists
        ioHandler.createParentDirIfNotExists(absPath);
        // write file to channel
        final long sizeInBytes = ioHandler.writeFile(absPath, inputStream);
        // save file info record
        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(fileName);
        f.setUploaderId(userId);
        f.setUploadTime(LocalDateTime.now());
        f.setUuid(uuid);
        f.setUserGroup(userGroup.getValue());
        f.setSizeInBytes(sizeInBytes);
        f.setFsGroupId(fsGroup.getId());
        fileInfoMapper.insert(f);
        return f;
    }

    @Override
    public FileInfo uploadFilesAsZip(int userId, String zipFile, String[] entryNames, FileUserGroupEnum userGroup,
                                     InputStream[] inputStreams) throws IOException {
        Objects.requireNonNull(entryNames);
        Objects.requireNonNull(userGroup);
        Objects.requireNonNull(zipFile);
        Objects.requireNonNull(inputStreams);
        if (inputStreams.length == 0)
            throw new IllegalArgumentException();
        if (entryNames.length != inputStreams.length)
            throw new IllegalArgumentException();

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();

        // find the first writable fs_group to use
        FsGroup fsGroup = fsGroupService.findFirstFsGroupForWrite();
        if (fsGroup == null)
            throw new NoWritableFsGroupException();

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
        SelectBasicFileInfoParam param = BeanCopyUtils.toType(reqVo, SelectBasicFileInfoParam.class);
        if (reqVo.filterForOwnedFilesOnly()) {
            param.setUploaderId(reqVo.getUserId());
        }
        IPage<FileInfo> dataList = fileInfoMapper.selectBasicInfoByUserIdSelective(forPage(reqVo.getPagingVo()), param);
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
    public void downloadFile(int id, @NotNull OutputStream outputStream) throws IOException {
        FileInfo fi = fileInfoMapper.selectByPrimaryKey(id);
        Objects.requireNonNull(fi, "Record not found");
        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        Objects.requireNonNull(fsg, "Unable to download file, because fs_group is not found");
        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        ioHandler.readFile(absPath, outputStream);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FileInfo findById(int id) {
        return fileInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public InputStream retrieveFileInputStream(int id) throws IOException {
        FileInfo fi = fileInfoMapper.selectDownloadInfoById(id);
        Objects.requireNonNull(fi, "Record not found");
        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        Objects.requireNonNull(fsg);
        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        return Files.newInputStream(Paths.get(absPath));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void validateUserDownload(int userId, int id) throws MsgEmbeddedException {
        // validate whether this file can be downloaded by current user
        FileInfo f = fileInfoMapper.selectValidateInfoById(id, userId);
        ValidUtils.requireNonNull(f, "File is not found or you are not allowed to download this file");
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getFilename(int id) {
        return fileInfoMapper.selectNameById(id);
    }

    @Override
    public void deleteFileLogically(int userId, int id) throws MsgEmbeddedException {
        // check if the file is owned by this user
        Integer uploaderId = fileInfoMapper.selectUploaderIdById(id);
        if (!Objects.equals(userId, uploaderId)) {
            throw new MsgEmbeddedException("You can only delete file that you uploaded");
        }
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

    // ------------------------------------- private helper methods

    private List<ZipCompressEntry> prepareZipEntries(String[] entryNames, InputStream[] entries) {
        List<ZipCompressEntry> l = new ArrayList<>(entries.length);
        for (int i = 0; i < entries.length; i++)
            l.add(new ZipCompressEntry(entryNames[i], entries[i]));
        return l;
    }
}
