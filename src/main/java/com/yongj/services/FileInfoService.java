package com.yongj.services;

import com.yongj.dao.FileInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.ParamInvalidException;
import com.yongj.vo.FileInfoVo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    FileInfo uploadFile(int userId, String fileName, FileUserGroupEnum userGroup, InputStream inputStream) throws IOException;

    /**
     * Find file info for user
     *
     * @param userId user.id
     */
    List<FileInfoVo> findFilesForUser(int userId);

    /**
     * Download file via uuid to the given outputStream
     *
     * @param userId       user.id
     * @param uuid         uuid
     * @param outputStream outputStream
     */
    void downloadFile(int userId, String uuid, OutputStream outputStream) throws IOException, ParamInvalidException;
}
