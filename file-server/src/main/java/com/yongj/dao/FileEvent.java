package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.*;

import com.yongj.enums.FEventType;
import lombok.*;
import com.curtisnewbie.common.dao.DaoSkeleton;

/**
 * File Events
 *
 * @author yongj.zhuang
 */
@Data
@TableName(value = "file_event")
public class FileEvent extends DaoSkeleton {

    /** event type */
    @TableField("type")
    private FEventType type;

    /** file key */
    @TableField("file_key")
    private String fileKey;

}
