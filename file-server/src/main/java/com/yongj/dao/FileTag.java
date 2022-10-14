package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author yongjie.zhuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_tag")
public class FileTag extends DaoSkeleton {

    /** id of file_info */
    @TableField("file_id")
    private Integer fileId;

    /** id of tag */
    @TableField("tag_id")
    private Integer tagId;

    /** id of user who created this file_tag relation */
    @TableField("user_id")
    private Integer userId;

}
