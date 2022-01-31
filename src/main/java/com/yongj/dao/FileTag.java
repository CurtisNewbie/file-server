package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import lombok.Data;


/*
CREATE TABLE IF NOT EXISTS file_tag (
    id INT UNSIGNED PRIMARY KEY AUTO_INCREMENT COMMENT "primary key",
    file_id INT UNSIGNED NOT NULL COMMENT "",
    tag_id INT UNSIGNED NOT NULL COMMENT "",
    user_id INT UNSIGNED NOT NULL COMMENT '',
    create_time DATETIME NOT NULL DEFAULT NOW() COMMENT 'when the record is created',
    create_by VARCHAR(255) NOT NULL COMMENT 'who created this record',
    update_time DATETIME COMMENT 'when the record is updated',
    update_by VARCHAR(255) COMMENT 'who updated this record',
    is_del TINYINT NOT NULL DEFAULT '0' COMMENT '0-normal, 1-deleted',
    CONSTRAINT uk_file_tag UNIQUE (file_id, tag_id)
) ENGINE=InnoDB comment 'join table between file and tag';
 */

/**
 * @author yongjie.zhuang
 */
@Data
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
