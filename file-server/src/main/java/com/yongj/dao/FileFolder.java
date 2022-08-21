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
@TableName(value = "file_folder")
public class FileFolder extends DaoSkeleton {

    /** folder no */
    @TableField("folder_no")
    private String folderNo;

    /** file's uuid */
    @TableField("uuid")
    private String uuid;

}
