package com.yongj.services;

import com.yongj.dao.FileInfo;
import com.yongj.dao.FileInfoMapper;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import com.yongj.vo.FileInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    public FileInfo saveFileInfo(int userId, String fileName, FileUserGroupEnum userGroup, InputStream inputStream) throws IOException {
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(userGroup);
        Objects.requireNonNull(inputStream);

        // assign random uuid
        final String uuid = UUID.randomUUID().toString();

        // save file info record
        FileInfo f = new FileInfo();
        f.setIsLogicDeleted(FileLogicDeletedEnum.NORMAL.getValue());
        f.setIsPhysicDeleted(FilePhysicDeletedEnum.NORMAL.getValue());
        f.setName(fileName);
        f.setUploaderId(userId);
        f.setUploadTime(new Date());
        f.setUuid(uuid);
        f.setUserGroup(userGroup.getValue());
        mapper.insert(f);

        // resolve absolute path
        final String absPath = pathResolver.resolveAbsolutePath(uuid, userId);

        // write file to channel
        ioHandler.writeByChannel(absPath, inputStream);
        return f;
    }

    @Override
    public List<FileInfoVo> findFilesForUser(int userId) {
        return null;
    }

    @Override
    public void downloadFile(String uuid, ServletOutputStream outputStream) {

    }
}
