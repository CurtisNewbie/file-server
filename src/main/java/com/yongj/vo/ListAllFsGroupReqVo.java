package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import lombok.Data;

/**
 * Request vo for listing all fsGroups
 *
 * @author yongjie.zhuang
 */
@Data
public class ListAllFsGroupReqVo {

    private FsGroupVo fsGroup;

    private PagingVo pagingVo;
}
