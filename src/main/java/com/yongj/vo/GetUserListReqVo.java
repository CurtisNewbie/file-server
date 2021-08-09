package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.io.Serializable;

/**
 * Get user list request vo
 *
 * @author yongjie.zhuang
 */
@Data
public class GetUserListReqVo extends PageableVo implements Serializable {

    /**
     * Username
     */
    private String username;

    /**
     * role
     */
    private String role;

    /**
     * is user disabled
     */
    private Integer isDisabled;

}
