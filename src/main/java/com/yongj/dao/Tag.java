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
public class Tag extends DaoSkeleton {

    /** name of tag */
    @TableField("name")
    private String name;

    /** user who owns this tag (tags are isolated between different users) */
    @TableField("user_id")
    private Integer userId;

}
