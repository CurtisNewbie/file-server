package com.yongj.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileInfoMapper {
    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table file_info
     *
     * @mbg.generated Fri Jun 11 14:43:17 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table file_info
     *
     * @mbg.generated Fri Jun 11 14:43:17 CST 2021
     */
    int insert(FileInfo record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table file_info
     *
     * @mbg.generated Fri Jun 11 14:43:17 CST 2021
     */
    FileInfo selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table file_info
     *
     * @mbg.generated Fri Jun 11 14:43:17 CST 2021
     */
    List<FileInfo> selectAll();

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table file_info
     *
     * @mbg.generated Fri Jun 11 14:43:17 CST 2021
     */
    int updateByPrimaryKey(FileInfo record);

    /**
     * Select name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserId(@Param("userId") int userId);

    /**
     * Select name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserIdAndName(@Param("userId") int userId, @Param("name") String name);

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
}