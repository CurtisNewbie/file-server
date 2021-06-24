package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class UserInfoVo {

    /** id */
    private Integer id;

    /**
     * username
     */
    private String username;

    /**
     * role
     */
    private String role;

    /** whether the user is disabled, 0-normal, 1-disabled */
    private Integer isDisabled;
}
