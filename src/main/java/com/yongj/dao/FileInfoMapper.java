package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * Select name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserId(@Param("userId") int userId);

    /**
     * Select fi.id, fi.name, fi.uuid, fi.size_in_bytes, fi.user_group, fi.uploader_id, fi.uploader_name,
     * fi.upload_time
     */
    IPage<FileInfo> selectFileListForUserSelective(Page<?> page, @Param("p") SelectFileInfoListParam param);

    /**
     * Select fi.id, fi.name, fi.uuid, fi.size_in_bytes, fi.user_group, fi.uploader_id, fi.uploader_name,
     * fi.upload_time
     */
    IPage<FileInfo> selectFileListForUserAndTag(Page<?> page, @Param("userId") int userId, @Param("tagName") String tagName,
                                                @Nullable @Param("filename") String filename);

    /**
     * Select user_group, user_id, is_logic_deleted
     */
    FileInfo selectValidateInfoById(@Param("id") int id, @Param("userId") int userId);

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
    IPage<FileInfo> findInfoForPhysicalDeleting(Page p);

    /**
     * Mark file being physically deleted
     *
     * @param id         id
     * @param deleteDate the date that is marked as physically deleted
     */
    void markFilePhysicDeleted(@Param("id") int id, @Param("deleteDate") LocalDateTime deleteDate);

    /**
     * Select fs_group_id, uuid, uploader_id
     */
    FileInfo selectDownloadInfoById(int id);
}