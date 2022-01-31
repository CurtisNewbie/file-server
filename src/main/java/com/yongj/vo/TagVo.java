package com.yongj.vo;

import com.curtisnewbie.common.dao.IsDel;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yongjie.zhuang
 */
@Data
public class TagVo {

    /** primary key */
    private Integer id;

    /** name of tag */
    private String name;

    /** user who owns this tag (tags are isolated between different users) */
    private Integer userId;

    /** when the record is created */
    private LocalDateTime createTime;

    /** who created this record */
    private String createBy;

    /** when the record is updated */
    private LocalDateTime updateTime;

    /** who updated this record */
    private String updateBy;

    /** whether current record is deleted */
    private IsDel isDel;
}
