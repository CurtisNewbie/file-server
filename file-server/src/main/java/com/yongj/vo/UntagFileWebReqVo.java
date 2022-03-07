package com.yongj.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author yongjie.zhuang
 */
@Data
public class UntagFileWebReqVo {

    @NotNull
    private Integer fileId;

    @NotBlank
    private String tagName;
}
