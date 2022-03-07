package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class ListFileExtReqVo extends PageableVo {

    /**
     * name of file extension, e.g., "txt"
     */
    private String name;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    private Integer isEnabled;

    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(getPagingVo());
    }
}
