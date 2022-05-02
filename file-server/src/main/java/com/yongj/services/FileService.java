package com.yongj.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PageableVo;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.vo.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
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
public interface FileService {

    /**
     * Grant file's access to other user
     *
     * @param cmd cmd
     */
    void grantFileAccess(@NotNull GrantFileAccessCmd cmd);

    /**
     * Save a single file from app
     */
    FileInfo uploadAppFile(@NotNull UploadAppFileCmd cmd) throws IOException;

    /**
     * Save a single file from user
     */
    FileInfo uploadFile(@NotNull UploadFileVo param) throws IOException;

    /**
     * Save multiple files as a single zip
     */
    FileInfo uploadFilesAsZip(@NotNull UploadZipFileVo param) throws IOException;

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
    @Deprecated
    // todo doesn't seem to be useful, consider removing it
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
     * Retrieve file's inputStream via id
     *
     * @param uuid file's uuid
     */
    InputStream retrieveFileInputStream(@NotNull String uuid) throws IOException;

    /**
     * Validate whether current app can download this file
     *
     * @param appName
     * @param fileId  id of file_info
     */
    void validateAppDownload(@NotBlank String appName, int fileId);

    /**
     * Validate whether current user can download this file
     *
     * @param userId user.id
     */
    void validateUserDownload(int userId, int fileId);

    /**
     * Logically delete the file
     *
     * @param userId id of the user
     * @param fileId file's id
     */
    void deleteFileLogically(int userId, int fileId);

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
    void updateFileUserGroup(int id, @NotNull FileUserGroupEnum fug, int userId, @Nullable String updatedBy);

    /**
     * Update file's info
     */
    void updateFile(@NotNull UpdateFileCmd cmd);

    /**
     * List granted file's accesses
     *
     * @param fileId file_info.id
     * @param userId id of user
     * @param paging info
     */
    PageablePayloadSingleton<List<FileSharingVo>> listGrantedAccess(int fileId, int userId, @NotNull PagingVo paging);

    /**
     * Remove granted file access
     *
     * @param fileId          file's id
     * @param userId          user's id
     * @param removedByUserId id of user to removed the access
     */
    void removeGrantedAccess(int fileId, int userId, int removedByUserId);

    /**
     * Update uploaderName
     */
    void fillBlankUploaderName(int fileId, @NotNull String uploaderName);

    /**
     * Tag a file
     */
    void tagFile(@Validated @NotNull TagFileCmd cmd);

    /**
     * Untag a file
     */
    void untagFile(@Validated @NotNull UntagFileCmd cmd);

    /**
     * List all file tags for user
     *
     * @param userId id of user
     * @return tag names
     */
    List<String> listFileTags(final int userId);

    /**
     * List all tags for current user and file
     *
     * @param userId id of user
     * @param fileId id of file
     * @return tag names
     */
    PageableVo<List<TagVo>> listFileTags(final int userId, final int fileId, final Page<?> page);

    /**
     * Check if the user if the owner of the file
     */
    boolean isFileOwner(int userId, int fileId);
}
