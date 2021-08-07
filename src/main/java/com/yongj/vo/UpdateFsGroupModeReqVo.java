package com.yongj.vo;

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
}
