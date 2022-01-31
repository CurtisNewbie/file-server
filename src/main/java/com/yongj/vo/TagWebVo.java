package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime createTime;

    /** who created this record */
    private String createBy;
}
