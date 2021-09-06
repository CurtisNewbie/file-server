package com.yongj.dao;

import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FileInfoMapper {

    int insert(FileInfo record);

    FileInfo selectByPrimaryKey(Integer id);

    List<FileInfo> selectAll();

    int updateByPrimaryKey(FileInfo record);

    /**
     * Select name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserId(@Param("userId") int userId);

    /**
     * Select id, name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserIdSelective(SelectBasicFileInfoParam param);

    /**
     * Select user_group, user_id, is_logic_deleted
     */
    FileValidateInfo selectValidateInfoById(@Param("id") int id);

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
    void markFilePhysicDeleted(@Param("id") int id, @Param("deleteDate") Date deleteDate);

    /**
     * Update user_group
     *
     * @param id        id
     * @param userGroup userGroup
     */
    void updateFileUserGroup(@Param("id") int id, @Param("userGroup") int userGroup);

    /**
     * Select fs_group_id, uuid, uploader_id
     */
    FileInfo selectDownloadInfoById(int id);
}