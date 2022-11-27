package com.yongj.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("createDate")
    private LocalDateTime createTime;

    /** created by */
    @JsonProperty("createdBy")
    private String createBy;
}

