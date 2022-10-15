package com.yongj.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * @author yongjie.zhuang
 */
@Data
public class ExtendsTokenExpReqVo {

    @NotEmpty
    private String token;

}
