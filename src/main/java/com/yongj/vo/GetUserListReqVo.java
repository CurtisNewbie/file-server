package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Get user list request vo
 *
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
public class GetUserListReqVo implements Serializable {

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

    /**
     * paging param
     */
    private PagingVo pagingVo;

}
