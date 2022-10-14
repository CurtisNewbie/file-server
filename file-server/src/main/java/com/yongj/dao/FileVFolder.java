package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.*;

import lombok.*;
import com.curtisnewbie.common.dao.DaoSkeleton;

/**
 * File and vfolder join table
 *
 * @author yongj.zhuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "file_vfolder")
public class FileVFolder extends DaoSkeleton {

    /** folder no */
    @TableField("folder_no")
    private String folderNo;

    /** file's uuid */
    @TableField("uuid")
    private String uuid;

}
