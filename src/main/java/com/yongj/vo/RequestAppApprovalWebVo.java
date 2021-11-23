package com.yongj.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yongjie.zhuang
 */
@Data
public class RequestAppApprovalWebVo {

    /** appId */
    @NotNull
    private Integer appId;
}
