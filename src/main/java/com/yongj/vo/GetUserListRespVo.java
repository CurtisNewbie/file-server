package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * Get user list response vo
 *
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
public class GetUserListRespVo implements Serializable {

    private Iterable<UserInfoFsVo> fileInfoList;

    private PagingVo pagingVo;

}
