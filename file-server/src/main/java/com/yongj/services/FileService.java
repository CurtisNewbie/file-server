package com.yongj.services;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileType;
import com.yongj.vo.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
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
     * Save a single file from user
     */
    FileInfo uploadFile(@Validated @NotNull UploadFileVo param) throws IOException;

    /**
     * Save multiple files as a single zip
     */
    FileInfo uploadFilesAsZip(@NotNull UploadZipFileVo param) throws IOException;

    /**
     * List files for user purely based on user_file_access
     *
     * @param reqVo filter and paging parameter
     */
    PageableList<FileInfoVo> listFilesByAccess(@NotNull ListFileInfoReqVo reqVo);

    /**
     * Find file info for user (with pagination)
     *
     * @param reqVo filter and paging parameter
     */
    PageableList<FileInfoVo> findPagedFilesForUser(@NotNull ListFileInfoReqVo reqVo);

    /**
     * Find logically deleted, but not physically deleted files
     */
    List<PhysicDeleteFileVo> findPagedFileIdsForPhysicalDeleting();

    /**
     * File uploader id of files that doesn't contain uploader name
     */
    List<FileUploaderInfoVo> findFilesWithoutUploaderName(int limit);

    /**
     * Find by id
     */
    FileInfo findById(int id);

    /**
     * Find by key
     */
    FileInfo findByKey(@NotEmpty String uuid);

    /**
     * Retrieve file's inputStream via id
     *
     * @param id file's id
     */
    InputStream retrieveFileInputStream(int id) throws IOException;

    /**
     * Retrieve file's FileChannel via id
     *
     * @param id file's id
     */
    FileChannel retrieveFileChannel(int id) throws IOException;

    /**
     * Retrieve file's inputStream via id
     *
     * @param uuid file's uuid
     */
    InputStream retrieveFileInputStream(@NotNull String uuid) throws IOException;

    /**
     * Validate whether current user can download this file
     *
     * @param userId user.id
     */
    void validateUserDownload(int userId, int fileId, @NotEmpty String userNo);

    /**
     * Move current file into another folder
     *
     * @param userid         userId
     * @param uuid           current file uuid
     * @param parentFileUuid parent file uuid
     */
    void moveFileInto(int userid, @NotEmpty String uuid, @Nullable String parentFileUuid);

    /**
     * Logically delete the file
     *
     * @param userId id of the user
     * @param uuid   uuid
     */
    void deleteFileLogically(int userId, String uuid);

    /**
     * Physically delete the file (this method should be invoked by the scheduler
     *
     * @param id id of the file
     */
    void markFileDeletedPhysically(int id);

    /**
     * Update file's info
     */
    void updateFile(@NotNull UpdateFileCmd cmd);

    /**
     * List granted file's accesses
     *
     * @param fileId file_info.id
     * @param userId id of user
     * @param page   page
     */
    PageableList<FileSharingVo> listGrantedAccess(int fileId, int userId, @NotNull Page page);

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
    PageableList<TagVo> listFileTags(final int userId, final int fileId, final Page<?> page);

    /**
     * Check if the user if the owner of the file
     */
    boolean isFileOwner(int userId, int fileId);

    /**
     * Check if the user if the owner of the file
     */
    boolean isFileOwner(int userId, @NotEmpty String uuid);

    /**
     * Find FileType by key
     */
    FileType findFileTypeByKey(@NotEmpty String uuid);

    /**
     * Make Directory
     */
    FileInfo mkdir(@NotNull @Valid MakeDirReqVo req);

    /**
     * List DIR type files
     */
    List<ListDirVo> listDirs(int userId);

    /**
     * List file keys in dir
     *
     * @param uuid uuid of dir
     * @return list of uuid of files in dir
     */
    List<String> listFilesInDir(String uuid, long limit, long offset);

    /**
     * Check if a file with same exists for current user
     *
     * @param fileName file's name
     * @param userId   user's id
     * @return exists
     */
    boolean filenameExists(@NotEmpty String fileName, int userId);

    /**
     * Export file as a zip
     */
    void exportAsZip(@NotNull ExportAsZipReq r, @NotNull TUser user);


    /**
     * Refresh user's file access
     * <p>
     * This is primarily used to generate the access records for the first time
     */
    void loadUserFileAccess();
}
