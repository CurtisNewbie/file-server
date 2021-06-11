package com.yongj.services;

import com.yongj.dao.FileInfo;
import com.yongj.dao.FileInfoMapper;
import com.yongj.dao.FileValidateInfo;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.ParamInvalidException;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import com.yongj.util.BeanCopyUtils;
import com.yongj.util.ValidUtils;
import com.yongj.vo.FileInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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

    @Override
    public FileInfo uploadFile(int userId, String fileName, FileUserGroupEnum userGroup, InputStream inputStream) throws IOException {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(userGroup);
        Objects.requireNonNull(inputStream);

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();
        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, userId);
        // create directories if not exists
        ioHandler.createParentDirIfNotExists(absPath);
        // write file to channel
        final long sizeInBytes = ioHandler.writeByChannel(absPath, inputStream);
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
        mapper.insert(f);
        return f;
    }

    @Override
    public List<FileInfoVo> findFilesForUser(int userId) {
        List<FileInfo> fList = mapper.selectBasicInfoByUserId(userId);
        return BeanCopyUtils.toTypeList(fList, FileInfoVo.class);
    }

    @Override
    public void downloadFile(String uuid, OutputStream outputStream) throws IOException, ParamInvalidException {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(outputStream);
        final Integer uploaderId = mapper.selectUploaderIdByUuid(uuid);
        Objects.requireNonNull(uploaderId);
        // read file from channel
        final String absPath = pathResolver.resolveAbsolutePath(uuid, uploaderId);
        ioHandler.readByChannel(absPath, outputStream);
    }

    @Override
    public void validateUserDownload(int userId, String uuid) throws ParamInvalidException {
        // validate whether this file can be downloaded by current user
        FileValidateInfo f = mapper.selectValidateInfoByUuid(uuid);
        ValidUtils.requireNonNull(f, "File not found");
        // file deleted
        ValidUtils.requireEquals(f.getIsLogicDeleted(), FileLogicDeletedEnum.NORMAL.getValue(), "File deleted already");
        // file belongs to private group && uploaderId unmatched
        if (!Objects.equals(f.getUserGroup(), FileUserGroupEnum.PUBLIC.getValue())
                && !Objects.equals(f.getUploaderId(), userId)) {
            throw new ParamInvalidException("You are not allowed to download this file");
        }
    }

    @Override
    public String getFilename(String uuid) {
        return mapper.selectNameByUuid(uuid);
    }
}
