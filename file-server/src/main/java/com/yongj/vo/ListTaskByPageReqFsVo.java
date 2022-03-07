package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

/**
 * Request vo for listing tasks in pages
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskByPageReqFsVo extends PageableVo {

    /** job's name */
    private String jobName;

    /** app group that runs this task */
    private String appGroup;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    private Integer enabled;
}