package com.yongj.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * file_sharing
 *
 * @author yongjie.zhuang
 */
@Data
public class FileSharingVo {

    /** id */
    private Integer id;

    /** user who now have access to the file */
    private Integer userId;

    /** time created */
    private LocalDateTime createTime;

    /** created by */
    private String createBy;
}

