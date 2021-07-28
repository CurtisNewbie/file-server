package com.yongj.services;

import com.github.pagehelper.PageInfo;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.ListFileInfoReqVo;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.vo.PhysicDeleteFileVo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Service for files
 *
 * @author yongjie.zhuang
 */
public interface FileInfoService {

    /**
     * Save a single file
     *
     * @param userId      user.id
     * @param fileName    fileName
     * @param userGroup   userGroup
     * @param inputStream file's inputStream
     * @return the saved fileInfo
     */
    FileInfo uploadFile(int userId, String fileName, FileUserGroupEnum userGroup, InputStream inputStream) throws IOException;

    /**
     * Save multiple files as a single zip
     *
     * @param userId       user.id
     * @param zipFileName  zipFileName
     * @param fileNames    entries' name
     * @param userGroup    userGroup
     * @param inputStreams files' inputStreams
     * @return the saved fileInfo
     */
    FileInfo uploadFilesAsZip(int userId, String zipFileName, String[] fileNames, FileUserGroupEnum userGroup, InputStream[] inputStreams)
            throws IOException;

    /**
     * Find file info for user
     *
     * @param userId user.id
     */
    List<FileInfoVo> findFilesForUser(int userId);

    /**
     * Find file info for user (with pagination)
     *
     * @param reqVo filter and paging parameter
     */
    PageInfo<FileInfoVo> findPagedFilesForUser(ListFileInfoReqVo reqVo);

    /**
     * Find logically deleted, but not physically deleted files' id (with pagination)
     *
     * @param pagingVo paging parameter
     */
    PageInfo<PhysicDeleteFileVo> findPagedFileIdsForPhysicalDeleting(PagingVo pagingVo);

    /**
     * Download file via uuid to the given outputStream
     *
     * @param uuid         uuid
     * @param outputStream outputStream
     */
    void downloadFile(String uuid, OutputStream outputStream) throws IOException;

    /**
     * Retrieve file's inputStream via uuid
     *
     * @param uuid uuid
     */
    InputStream retrieveFileInputStream(String uuid) throws IOException;

    /**
     * Validate whether current user can download this file
     *
     * @param userId user.id
     * @param uuid   uuid
     * @throws MsgEmbeddedException
     */
    void validateUserDownload(int userId, String uuid) throws MsgEmbeddedException;

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
    void deleteFileLogically(int userId, String uuid) throws MsgEmbeddedException;

    /**
     * Physically delete the file (this method should be invoked by the scheduler
     *
     * @param id id of the file
     */
    void markFileDeletedPhysically(int id);
}
