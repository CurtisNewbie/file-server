package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.*;

import lombok.*;
import com.curtisnewbie.common.dao.DaoSkeleton;

/**
 * User and Virtual folder join table
 *
 * @author yongj.zhuang
 */
@Data
@TableName(value = "user_vfolder")
public class UserVFolder extends DaoSkeleton {

    /** user no */
    @TableField("user_no")
    private String userNo;

    /** folder no */
    @TableField("folder_no")
    private String folderNo;

    /** ownership */
    @TableField("ownership")
    private String ownership;

    /** granted by (user_no) */
    @TableField("granted_by")
    private String grantedBy;

}
