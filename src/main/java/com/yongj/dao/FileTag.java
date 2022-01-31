package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
@TableName("file_tag")
public class FileTag extends DaoSkeleton {

    /**
     * FileInfo.id
     */
    @TableField("file_id")
    private Integer fileId;

    /**
     * tag's name
     */
    @TableField("name")
    private String name;

    /**
     * User.id
     */
    @TableField("user_id")
    private Integer userId;
}
