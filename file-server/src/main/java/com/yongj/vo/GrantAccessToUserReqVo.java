package com.yongj.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author yongjie.zhuang
 */
@Data
public class GrantAccessToUserReqVo {

    /** file's id */
    @NotNull
    private Integer fileId;

    /** name of user who is granted access to the file */
    @NotEmpty
    private String grantedTo;
}
