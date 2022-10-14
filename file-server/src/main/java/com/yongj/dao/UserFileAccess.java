package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.*;

import com.yongj.enums.FileAccessType;
import lombok.*;
import com.curtisnewbie.common.dao.DaoSkeleton;

/**
 * User file access
 *
 * @author yongj.zhuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "user_file_access")
public class UserFileAccess extends DaoSkeleton {

    /** user no */
    @TableField("user_no")
    private String userNo;

    /** file key */
    @TableField("file_uuid")
    private String fileUuid;

    /** Access Type */
    @TableField("access_type")
    private FileAccessType accessType;

}
