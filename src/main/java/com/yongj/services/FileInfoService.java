package com.yongj.services;

import com.github.pagehelper.PageInfo;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.ParamInvalidException;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.ListFileInfoReqVo;

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
     * Find file info for user (with pagination)
     *
     * @param reqVo  filter and paging parameter
     */
    PageInfo<FileInfoVo> findPagedFilesForUser(ListFileInfoReqVo reqVo);

    /**
     * Download file via uuid to the given outputStream
     *
     * @param uuid         uuid
     * @param outputStream outputStream
     */
    void downloadFile(String uuid, OutputStream outputStream) throws IOException, ParamInvalidException;

    /**
     * Validate whether current user can download this file
     *
     * @param userId user.id
     * @param uuid   uuid
     * @throws ParamInvalidException
     */
    void validateUserDownload(int userId, String uuid) throws ParamInvalidException;

    /**
     * Get filename of file
     *
     * @param uuid uuid
     */
    String getFilename(String uuid);

    /**
     * Logically delete the file
     *
     * @param userId id of the user
     * @param uuid   uuid
     */
    void deleteFileLogically(int userId, String uuid) throws ParamInvalidException;
}
