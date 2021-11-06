package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
@TableName("file_extension")
public class FileExtension {

    /**
     * primary key
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * name of file extension, e.g., "txt"
     */
    @TableField("name")
    private String name;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    @TableField("is_enabled")
    private Integer isEnabled;
}
