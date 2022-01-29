package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("file_info")
public class FileInfo {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /** name of the file */
    @TableField("name")
    private String name;

    /** file's uuid */
    @TableField("uuid")
    private String uuid;

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    @TableField("is_logic_deleted")
    private Integer isLogicDeleted;

    /** whether the file is physically deleted, 0-normal, 1-deleted */
    @TableField("is_physic_deleted")
    private Integer isPhysicDeleted;

    /** size of file in bytes */
    @TableField("size_in_bytes")
    private Long sizeInBytes;

    /** uploader id, i.e., user.id */
    @TableField("uploader_id")
    private Integer uploaderId;

    /** uploader name */
    @TableField("uploader_name")
    private String uploaderName;

    /** upload time */
    @TableField("uploader_time")
    private LocalDateTime uploadTime;

    /** when the file is logically deleted */
    @TableField("logic_delete_time")
    private LocalDateTime logicDeleteTime;

    /** when the file is physically deleted */
    @TableField("physic_delete_time")
    private LocalDateTime physicDeleteTime;

    /** the group that the file belongs to, 0-public, 1-private */
    @TableField("user_group")
    private Integer userGroup;

    /** id of fs_group */
    @TableField("fs_group_id")
    private Integer fsGroupId;
}