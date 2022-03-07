package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.util.Date;

/**
 * @author yongjie.zhuang
 */
@Data
public class ListTaskHistoryByPageReqWebVo extends PageableVo {

    /** Task id */
    private Integer taskId;

    /** task's name */
    private String jobName;

    /** start time */
    private Date startTime;

    /** end time */
    private Date endTime;

    /** task triggered by */
    private String runBy;
}
