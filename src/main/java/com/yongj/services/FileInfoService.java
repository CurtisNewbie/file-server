package com.yongj.services;

import com.yongj.dao.FileInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.vo.FileInfoVo;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author yongjie.zhuang
 */
public interface FileInfoService {

    /**
     * Save file info (including the actual file)
     *
     * @param userId      user.id
     * @param fileName    fileName
     * @param userGroup   userGroup
     * @param inputStream file's inputStream
     * @return the saved fileInfo
     */
    FileInfo saveFileInfo(int userId, String fileName, FileUserGroupEnum userGroup, InputStream inputStream) throws IOException;

    /**
     * Find file info for user
     *
     * @param userId user.id
     */
    List<FileInfoVo> findFilesForUser(int userId);

    /**
     * Download file via uuid to the given outputStream
     *
     * @param uuid         uuid
     * @param outputStream outputStream
     */
    void downloadFile(String uuid, ServletOutputStream outputStream);
}
