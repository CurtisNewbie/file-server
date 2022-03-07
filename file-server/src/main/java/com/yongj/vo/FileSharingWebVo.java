package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * file_sharing
 *
 * @author yongjie.zhuang
 */
@Data
public class FileSharingWebVo {

    /** id */
    private Integer id;

    /** user who now have access to the file */
    private Integer userId;

    /** name of user who now have access to the file */
    private String username;

    /** time created */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime createDate;

    /** created by */
    private String createdBy;
}

