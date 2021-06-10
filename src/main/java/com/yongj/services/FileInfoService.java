package com.yongj.services;

import com.yongj.vo.FileInfoVo;

import javax.servlet.ServletOutputStream;
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
     */
    void saveFileInfo(int userId, String fileName, String userGroup, InputStream inputStream);

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
