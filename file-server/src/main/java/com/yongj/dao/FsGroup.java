package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import lombok.Data;

/**
 * FileSystem group, used to differentiate which base folder or mounted folder should be used
 *
 * @author yongjie.zhuang
 */
@Data
@TableName("fs_group")
public class FsGroup extends DaoSkeleton {

    /** group name */
    @TableField("name")
    private String name;

    /** base folder */
    @TableField("base_folder")
    private String baseFolder;

    /** mode: 1-read, 2-read/write */
    @TableField("mode")
    private Integer mode;

}