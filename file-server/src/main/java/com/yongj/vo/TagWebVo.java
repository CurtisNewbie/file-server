package com.yongj.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yongjie.zhuang
 */
@Data
public class TagWebVo {

    private Integer id;

    /** name of tag */
    private String name;

    /** when the record is created */
    private LocalDateTime createTime;

    /** who created this record */
    private String createBy;
}
