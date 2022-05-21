package com.yongj.vo;

import com.curtisnewbie.common.util.AssertUtils;
import com.yongj.enums.FsGroupMode;
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
    private FsGroupMode mode;

    public void validate() {
        AssertUtils.notNull(getId());
        AssertUtils.notNull(getMode());
    }
}
