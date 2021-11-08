package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.ValidUtils;
import lombok.Data;

/**
 * Request vo for updating fs_group's mode
 *
 * @author yongjie.zhuang
 */
@Data
public class UpdateFsGroupModeReqVo {

    /** id of fs_group */
    private Integer id;

    /** mode */
    private Integer mode;

    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(getId());
        ValidUtils.requireNonNull(getMode());
    }
}
