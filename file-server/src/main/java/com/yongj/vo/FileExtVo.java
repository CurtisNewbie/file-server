package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yongj.enums.FExtIsEnabled;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileExtVo {

    /**
     * primary key
     */
    private Integer id;

    /**
     * name of file extension, e.g., "txt"
     */
    private String name;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    private FExtIsEnabled isEnabled;

    private String createBy;

    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime createTime;

    private String updateBy;

    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime updateTime;
}
