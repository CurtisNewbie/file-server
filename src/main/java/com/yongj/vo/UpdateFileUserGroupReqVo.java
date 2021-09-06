package com.yongj.vo;

import lombok.Data;

/**
 * Request vo for updating file's user_group
 *
 * @author yongjie.zhuang
 */
@Data
public class UpdateFileUserGroupReqVo {

    /** UUID */
    private Integer id;

    /** user_group */
    private Integer userGroup;
}
