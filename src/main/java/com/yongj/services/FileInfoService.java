package com.yongj.services;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.vo.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Service for files
 *
 * @author yongjie.zhuang
 */
@Validated
public interface FileInfoService {

    /**
     * Grant file's access to other user
     *
     * @param cmd cmd
     */
    void grantFileAccess(@NotNull GrantFileAccessCmd cmd) throws MsgEmbeddedException;

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
     * Find file info for user (with pagination)
     *
     * @param reqVo filter and paging parameter
     */
    PageablePayloadSingleton<List<FileInfoVo>> findPagedFilesForUser(@NotNull ListFileInfoReqVo reqVo);

    /**
     * Find logically deleted, but not physically deleted files' id (with pagination)
     *
     * @param pagingVo paging parameter
     */
    PageablePayloadSingleton<List<PhysicDeleteFileVo>> findPagedFileIdsForPhysicalDeleting(@NotNull PagingVo pagingVo);

    /**
     * File uploader id of files that doesn't contain uploader name
     */
    PageablePayloadSingleton<List<FileUploaderInfoVo>> findPagedFilesWithoutUploaderName(@NotNull PagingVo pagingVo);

    /**
     * Download file via id to the given outputStream
     *
     * @param id           file's id
     * @param outputStream outputStream
     */
    void downloadFile(int id, @NotNull OutputStream outputStream) throws IOException;

    /**
     * Find by id
     */
    FileInfo findById(int id);

    /**
     * Retrieve file's inputStream via id
     *
     * @param id file's id
     */
    InputStream retrieveFileInputStream(int id) throws IOException;

    /**
     * Validate whether current user can download this file
     *
     * @param userId user.id
     * @throws MsgEmbeddedException
     */
    void validateUserDownload(int userId, int fileId) throws MsgEmbeddedException;

    /**
     * Get filename of file
     *
     * @param id file's id
     */
    String getFilename(int id);

    /**
     * Logically delete the file
     *
     * @param userId id of the user
     * @param fileId file's id
     */
    void deleteFileLogically(int userId, int fileId) throws MsgEmbeddedException;

    /**
     * Physically delete the file (this method should be invoked by the scheduler
     *
     * @param id id of the file
     */
    void markFileDeletedPhysically(int id);

    /**
     * Update file's userGroup
     *
     * @param id     file's id
     * @param fug    fileUserGroup
     * @param userId who updated this file
     */
    void updateFileUserGroup(int id, @NotNull FileUserGroupEnum fug, int userId) throws MsgEmbeddedException;

    /**
     * Update file's info
     */
    void updateFile(@NotNull UpdateFileCmd cmd);

    /**
     * List granted file's accesses
     *
     * @param fileId file_info.id
     * @param paging info
     */
    PageablePayloadSingleton<List<FileSharingVo>> listGrantedAccess(int fileId, @NotNull PagingVo paging);

    /**
     * Remove granted file access
     *
     * @param fileId    file's id
     * @param userId    user's id
     * @param removedBy id of user to removed the access
     */
    void removeGrantedAccess(int fileId, int userId, int removedBy);

    /**
     * Update uploaderName
     */
    void updateUploaderName(int fileId, @NotNull String uploaderName);
}
