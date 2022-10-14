package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.curtisnewbie.common.dao.DaoSkeleton;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * file_sharing
 *
 * @author yongjie.zhuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("file_sharing")
public class FileSharing extends DaoSkeleton {

    /** id */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /** id of file_info */
    @TableField("file_id")
    private Integer fileId;

    /** user who now have access to the file */
    @TableField("user_id")
    private Integer userId;
}

