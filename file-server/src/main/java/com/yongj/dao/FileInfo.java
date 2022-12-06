package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import com.yongj.enums.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_info")
public class FileInfo extends DaoSkeleton {

    /** name of the file */
    @TableField("name")
    private String name;

    /** file's uuid */
    @TableField("uuid")
    private String uuid;

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    @TableField("is_logic_deleted")
    private FLogicDelete isLogicDeleted;

    /** whether the file is physically deleted, 0-normal, 1-deleted */
    @TableField("is_physic_deleted")
    private FPhysicDelete isPhysicDeleted;

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
    @TableField("upload_time")
    private LocalDateTime uploadTime;

    /** when the file is logically deleted */
    @TableField("logic_delete_time")
    private LocalDateTime logicDeleteTime;

    /** when the file is physically deleted */
    @TableField("physic_delete_time")
    private LocalDateTime physicDeleteTime;

    /** the group that the file belongs to, 0-public, 1-private */
    @TableField("user_group")
    private FUserGroup userGroup;

    /** id of fs_group */
    @TableField("fs_group_id")
    private Integer fsGroupId;

    /** file type: FILE, DIR */
    @TableField("file_type")
    private FileType fileType;

    /** parent file's uuid */
    @TableField("parent_file")
    private String parentFile;

    public boolean isDir() {
        return fileType == FileType.DIR;
    }

    public boolean isFile() {
        return fileType == FileType.FILE;
    }

    public boolean belongsTo(int userId) {
        return this.uploaderId != null && this.uploaderId == userId;
    }
}
