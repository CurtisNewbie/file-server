package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import com.yongj.enums.FExtIsEnabled;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yongjie.zhuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("file_extension")
public class FileExtension extends DaoSkeleton {

    /**
     * name of file extension, e.g., "txt"
     */
    @TableField("name")
    private String name;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    @TableField("is_enabled")
    private FExtIsEnabled isEnabled;
}
