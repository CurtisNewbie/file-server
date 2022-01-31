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
     * Tag.id
     */
    @TableField("tag_id")
    private Integer tagId;
}
