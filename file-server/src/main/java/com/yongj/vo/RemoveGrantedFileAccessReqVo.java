package com.yongj.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yongjie.zhuang
 */
@Data
public class RemoveGrantedFileAccessReqVo {

    @NotNull
    private Integer fileId;

    @NotNull
    private Integer userId;

}
