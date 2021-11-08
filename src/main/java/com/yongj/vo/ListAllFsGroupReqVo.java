package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

/**
 * Request vo for listing all fsGroups
 *
 * @author yongjie.zhuang
 */
@Data
public class ListAllFsGroupReqVo extends PageableVo {

    private FsGroupVo fsGroup;

    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(getPagingVo());
        ValidUtils.requireNonNull(getPagingVo().getPage());
        ValidUtils.requireNonNull(getPagingVo().getLimit());
    }
}
