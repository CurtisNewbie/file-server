package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface FileInfoMapper extends BaseMapper<FileInfo> {

    int insert(FileInfo record);

    FileInfo selectByPrimaryKey(Integer id);

    List<FileInfo> selectAll();

    int updateByPrimaryKey(FileInfo record);

    /**
     * Select name, uuid, size_in_bytes
     */
    List<FileInfo> selectBasicInfoByUserId(@Param("userId") int userId);

    /**
     * Select fi.id, fi.name, fi.uuid, fi.size_in_bytes, fi.user_group, fi.uploader_id, fi.uploader_name,
     * fi.upload_time
     */
    IPage<FileInfo> selectBasicInfoByUserIdSelective(Page<?> page, @Param("p") SelectBasicFileInfoParam param);

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

    /**
     * Update user_group, name selectively
     */
    void updateInfo(FileInfo fi);
}