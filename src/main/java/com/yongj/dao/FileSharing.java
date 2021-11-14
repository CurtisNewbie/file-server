package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * file_sharing
 *
 * @author yongjie.zhuang
 */
@Data
@Builder
@TableName("file_sharing")
public class FileSharing {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** id of file_info */
    @TableField("file_id")
    private Integer fileId;

    /** user who now have access to the file */
    @TableField("user_id")
    private Integer userId;

    /** time created */
    @TableField("create_date")
    private LocalDateTime createDate;

    /** created by */
    @TableField("created_by")
    private String createdBy;

    /** time updated */
    @TableField("update_date")
    private LocalDateTime updateDate;

    /** updated by */
    @TableField("updated_by")
    private String updatedBy;

    /**  is deleted, 0: normal, 1: deleted */
    @TableField("is_del")
    private Integer isDel;
}

