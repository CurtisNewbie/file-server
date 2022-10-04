package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import com.yongj.enums.FsGroupType;
import lombok.Data;

import java.time.LocalDateTime;

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

    /** Type of a fs_group */
    @TableField("type")
    private FsGroupType type;

    /** size in bytes */
    @TableField("size")
    private Long size;

    /** previous scan time */
    @TableField("scan_time")
    private LocalDateTime scanTime;

}