package com.yongj.vo;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UntagFileCmd {

    private final int fileId;

    @NotBlank
    private final String tagName;

    @NotBlank
    private final String untaggedBy;

    private final int userId;
}
