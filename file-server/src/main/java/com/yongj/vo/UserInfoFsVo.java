package com.yongj.vo;

import lombok.Data;

/**
 * FileServer's view of UserInfo
 *
 * @author yongjie.zhuang
 */
@Data
public class UserInfoFsVo {

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
