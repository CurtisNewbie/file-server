package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.*;

import lombok.*;
import com.curtisnewbie.common.dao.DaoSkeleton;

/**
 * Virtual folder
 *
 * @author yongj.zhuang
 */
@Data
@TableName(value = "vfolder")
public class VFolder extends DaoSkeleton {

    /** folder no */
    @TableField("folder_no")
    private String folderNo;

    /** name of the folder */
    @TableField("name")
    private String name;

}
