package com.yongj.services;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.converters.FileInfoConverter;
import com.yongj.dao.*;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.NoWritableFsGroupException;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.io.ZipCompressEntry;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.ListFileInfoReqVo;
import com.yongj.vo.PhysicDeleteFileVo;
import com.yongj.vo.UpdateFileCmd;
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
    private FileInfoMapper mapper;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private FsGroupService fsGroupService;

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
        mapper.insert(f);
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
        mapper.insert(f);
        return f;
    }

    private List<ZipCompressEntry> prepareZipEntries(String[] entryNames, InputStream[] entries) {
        List<ZipCompressEntry> l = new ArrayList<>(entries.length);
        for (int i = 0; i < entries.length; i++)
            l.add(new ZipCompressEntry(entryNames[i], entries[i]));
        return l;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<FileInfoVo>> findPagedFilesForUser(@NotNull ListFileInfoReqVo reqVo) {
        SelectBasicFileInfoParam param = BeanCopyUtils.toType(reqVo, SelectBasicFileInfoParam.class);
        if (reqVo.filterForOwnedFilesOnly()) {
            param.setUploaderId(reqVo.getUserId());
        }
        IPage<FileInfo> dataList = mapper.selectBasicInfoByUserIdSelective(forPage(reqVo.getPagingVo()), param);
        return PagingUtil.toPageList(dataList, (e) -> {
            FileInfoVo v = fileInfoConverter.toVo(e);
            v.setIsOwner(Objects.equals(e.getUploaderId(), reqVo.getUserId()));
            return v;
        });
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<PhysicDeleteFileVo>> findPagedFileIdsForPhysicalDeleting(@NotNull PagingVo pagingVo) {
        IPage<FileInfo> dataList = mapper.findInfoForPhysicalDeleting(forPage(pagingVo));
        return PagingUtil.toPageList(dataList, fileInfoConverter::toPhysicDeleteFileVo);
    }

    @Override
    public void downloadFile(int id, @NotNull OutputStream outputStream) throws IOException {
        FileInfo fi = mapper.selectByPrimaryKey(id);
        Objects.requireNonNull(fi, "Record not found");
        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        Objects.requireNonNull(fsg, "Unable to download file, because fs_group is not found");
        final String absPath = pathResolver.resolveAbsolutePath(fi.getUuid(), fi.getUploaderId(), fsg.getBaseFolder());
        ioHandler.readFile(absPath, outputStream);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FileInfo findById(int id) {
        return mapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public InputStream retrieveFileInputStream(int id) throws IOException {
        FileInfo fi = mapper.selectDownloadInfoById(id);
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
        FileValidateQryInfo f = mapper.selectValidateInfoById(id);
        ValidUtils.requireNonNull(f, "File not found");
        // file deleted
        ValidUtils.requireEquals(f.getIsLogicDeleted(), FileLogicDeletedEnum.NORMAL.getValue(), "File deleted already");
        // file belongs to private group && uploaderId unmatched
        if (!Objects.equals(f.getUserGroup(), FileUserGroupEnum.PUBLIC.getValue())
                && !Objects.equals(f.getUploaderId(), userId)) {
            throw new MsgEmbeddedException("You are not allowed to download this file");
        }
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public String getFilename(int id) {
        return mapper.selectNameById(id);
    }

    @Override
    public void deleteFileLogically(int userId, int id) throws MsgEmbeddedException {
        // check if the file is owned by this user
        Integer uploaderId = mapper.selectUploaderIdById(id);
        if (!Objects.equals(userId, uploaderId)) {
            throw new MsgEmbeddedException("You can only delete file that you uploaded");
        }
        mapper.logicDelete(id);
    }

    @Override
    public void markFileDeletedPhysically(int id) {
        mapper.markFilePhysicDeleted(id, new Date());
    }

    @Override
    public void updateFileUserGroup(int id, @NotNull FileUserGroupEnum fug, int userId)
            throws MsgEmbeddedException {
        Integer uploader = mapper.selectUploaderIdById(id);
        if (uploader == null)
            throw new MsgEmbeddedException("File not found");

        if (!Objects.equals(uploader, userId))
            throw new MsgEmbeddedException("You are not allowed to update this file");

        mapper.updateFileUserGroup(id, fug.getValue());
    }

    @Override
    public void updateFile(@NotNull UpdateFileCmd cmd) {
        FileInfo fi = new FileInfo();
        fi.setId(cmd.getId());
        fi.setUserGroup(cmd.getUserGroup().getValue());
        fi.setName(cmd.getFileName());
        mapper.updateInfo(fi);
    }
}
