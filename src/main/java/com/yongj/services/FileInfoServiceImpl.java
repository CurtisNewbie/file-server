package com.yongj.services;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yongj.dao.*;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FileOwnership;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.io.ZipCompressEntry;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.ListFileInfoReqVo;
import com.yongj.vo.PhysicDeleteFileVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class FileInfoServiceImpl implements FileInfoService {

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
        Objects.requireNonNull(fsGroup, "No writable fs_group found, unable to upload file");

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
        f.setUploadTime(new Date());
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
        Objects.requireNonNull(fsGroup, "No writable fs_group found, unable to upload file");

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
        f.setUploadTime(new Date());
        f.setUuid(uuid);
        f.setUserGroup(userGroup.getValue());
        f.setSizeInBytes(sizeInBytes);
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
    public List<FileInfoVo> findFilesForUser(int userId) {
        List<FileInfo> fList = mapper.selectBasicInfoByUserId(userId);
        return BeanCopyUtils.toTypeList(fList, FileInfoVo.class);
    }

    @Override
    public PageInfo<FileInfoVo> findPagedFilesForUser(ListFileInfoReqVo reqVo) {
        Objects.requireNonNull(reqVo);
        Objects.requireNonNull(reqVo.getPagingVo());
        SelectBasicFileInfoParam param = BeanCopyUtils.toType(reqVo, SelectBasicFileInfoParam.class);
        if (reqVo.getOwnership() != null &&
                Objects.equals(FileOwnership.parse(reqVo.getOwnership()), FileOwnership.FILES_OF_THE_REQUESTER)) {
            param.setUploaderId(reqVo.getUserId());
        }
        Page page = PageHelper.startPage(reqVo.getPagingVo().getPage(), reqVo.getPagingVo().getLimit());
        List<FileInfoVo> voList = mapper.selectBasicInfoByUserIdSelective(param)
                .stream()
                .map(e -> {
                    FileInfoVo v = BeanCopyUtils.toType(e, FileInfoVo.class);
                    v.setIsOwner(Objects.equals(e.getUploaderId(), reqVo.getUserId()));
                    return v;
                }).collect(Collectors.toList());
        return PagingUtil.pageInfoOf(voList, page.getTotal());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageInfo<PhysicDeleteFileVo> findPagedFileIdsForPhysicalDeleting(PagingVo pagingVo) {
        Objects.requireNonNull(pagingVo);
        PageHelper.startPage(pagingVo.getPage(), pagingVo.getLimit());
        return BeanCopyUtils.toPageList(PageInfo.of(mapper.findInfoForPhysicalDeleting()), PhysicDeleteFileVo.class);
    }

    @Override
    public void downloadFile(@NotNull String uuid, @NotNull OutputStream outputStream) throws IOException {
        FileInfo fi = mapper.selectByUuid(uuid);
        Objects.requireNonNull(fi);
        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        Objects.requireNonNull(fsg);
        final String absPath = pathResolver.resolveAbsolutePath(uuid, fi.getUploaderId(), fsg.getBaseFolder());
        ioHandler.readFile(absPath, outputStream);
    }

    @Override
    public InputStream retrieveFileInputStream(@NotEmpty String uuid) throws IOException {
        FileInfo fi = mapper.selectByUuid(uuid);
        Objects.requireNonNull(fi);
        FsGroup fsg = fsGroupService.findFsGroupById(fi.getFsGroupId());
        Objects.requireNonNull(fsg);
        final String absPath = pathResolver.resolveAbsolutePath(uuid, fi.getUploaderId(), fsg.getBaseFolder());
        return Files.newInputStream(Paths.get(absPath));
    }

    @Override
    public void validateUserDownload(int userId, String uuid) throws MsgEmbeddedException {
        // validate whether this file can be downloaded by current user
        FileValidateInfo f = mapper.selectValidateInfoByUuid(uuid);
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
    public String getFilename(String uuid) {
        return mapper.selectNameByUuid(uuid);
    }

    @Override
    public void deleteFileLogically(int userId, String uuid) throws MsgEmbeddedException {
        // check if the file is owned by this user
        Integer uploaderId = mapper.selectUploaderIdByUuid(uuid);
        if (!Objects.equals(userId, uploaderId)) {
            throw new MsgEmbeddedException("You can only delete file that you uploaded");
        }
        mapper.logicDelete(uuid);
    }

    @Override
    public void markFileDeletedPhysically(int id) {
        mapper.markFilePhysicDeleted(id, new Date());
    }

    @Override
    public void updateFileUserGroup(@NotEmpty String uuid, @NotNull FileUserGroupEnum fug, int userId)
            throws MsgEmbeddedException {
        Integer uploader = mapper.selectUploaderIdByUuid(uuid);
        if (uploader == null)
            throw new MsgEmbeddedException("File not found");

        if (!Objects.equals(uploader, userId))
            throw new MsgEmbeddedException("You are not allowed to update this file");

        mapper.updateFileUserGroup(uuid, fug.getValue());
    }
}
