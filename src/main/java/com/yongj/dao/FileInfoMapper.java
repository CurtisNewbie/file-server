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
     * Select name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserIdSelective(SelectBasicFileInfoParam param);

    /**
     * Select user_group, user_id, is_logic_deleted
     */
    FileValidateInfo selectValidateInfoByUuid(@Param("uuid") String uuid);

    /**
     * Select name
     */
    String selectNameByUuid(@Param("uuid") String uuid);

    /**
     * Select uploader_id
     */
    Integer selectUploaderIdByUuid(@Param("uuid") String uuid);

    /**
     * Logically delete the file by uuid
     */
    void logicDelete(@Param("uuid") String uuid);

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
     * @param uuid      uuid
     * @param userGroup userGroup
     */
    void updateFileUserGroup(@Param("uuid") String uuid, @Param("userGroup") int userGroup);

    /**
     * Select *
     *
     * @param uuid uuid
     */
    FileInfo selectByUuid(String uuid);
}