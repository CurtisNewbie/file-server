package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Application File
 *
 * @author yongj.zhuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "app_file")
public class AppFile extends DaoSkeleton {

    /** name of the file */
    @TableField("name")
    private String name;

    /** file's uuid */
    @TableField("uuid")
    private String uuid;

    /** size of file in bytes */
    @TableField("size")
    private Long size;

    /** app name */
    @TableField("app_name")
    private String appName;

    /** owner's id */
    @TableField("user_id")
    private Integer userId;

    /** id of fs_group */
    @TableField("fs_group_id")
    private Integer fsGroupId;

}
