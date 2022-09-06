package com.yongj.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.util.EnhancedMapper;
import com.yongj.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yongj.zhuang
 */
public interface FileInfoMapper extends EnhancedMapper<FileInfo> {

    /**
     * Select fi.id, fi.name, fi.uuid, fi.size_in_bytes, fi.user_group, fi.uploader_id, fi.uploader_name,
     * fi.upload_time
     */
    List<FileInfo> selectFileListForUserSelective(Page<?> page, @Param("p") SelectFileInfoListParam param);

    /**
     * Count for {@link #selectFileListForUserSelective(Page, SelectFileInfoListParam)}
     */
    long countFileListForUserSelective(@Param("p") SelectFileInfoListParam param);

    /**
     * Select fi.id, fi.name, fi.uuid, fi.size_in_bytes, fi.user_group, fi.uploader_id, fi.uploader_name,
     * fi.upload_time
     */
    List<FileInfo> selectFileListForUserAndTag(Page<?> page, @Param("userId") int userId, @Param("tagName") String tagName,
                                               @Nullable @Param("filename") String filename);

    /**
     * Count for {@link #selectFileListForUserAndTag(Page, int, String, String)}
     */
    long countFileListForUserAndTag(@Param("userId") int userId, @Param("tagName") String tagName, @Nullable @Param("filename") String filename);

    /**
     * Select user_group, uploaderId, fileSharingId, fileFolderId, isLogicDeleted
     */
    FileDownloadValidInfo selectValidateInfoById(@Param("id") int fileId, @Param("userId") int userId, @Param("userNo") String userNo);

    /**
     * Select name
     */
    String selectNameById(@Param("id") int id);

    /**
     * Select uploader_id
     */
    Integer selectUploaderIdById(@Param("id") int id);

    /**
     * Logically delete the file by uuid
     */
    void logicDelete(@Param("id") int id);

    /**
     * Select id of files that are logically deleted but not physically deleted
     */
    List<FileInfo> findInfoForPhysicalDeleting();

    /**
     * Mark file being physically deleted
     *
     * @param id         id
     * @param deleteDate the date that is marked as physically deleted
     */
    void markFilePhysicDeleted(@Param("id") int id, @Param("deleteDate") LocalDateTime deleteDate);

    /**
     * Select fs_group_id, uuid, uploader_id, upload_type
     */
    FileInfo selectDownloadInfoById(int id);

}