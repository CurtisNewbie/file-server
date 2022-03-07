package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
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
public class GetUserListRespVo extends PageableVo implements Serializable {

    private Iterable<UserInfoFsVo> fileInfoList;
}
