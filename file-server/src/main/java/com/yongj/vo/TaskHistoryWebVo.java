package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.curtisnewbie.common.vo.PageableVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * task history
 *
 * @author yongjie.zhuang
 */
@NoArgsConstructor
@Data
public class TaskHistoryWebVo extends PageableVo {
    /** id */
    private Integer id;

    /** job name */
    private String jobName;

    /** task's id */
    private Integer taskId;

    /** start time */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private Date startTime;

    /** end time */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private Date endTime;

    /** task triggered by */
    private String runBy;

    /** result of last execution */
    private String runResult;

    @Builder
    public TaskHistoryWebVo(Integer id, String jobName, Integer taskId, Date startTime, Date endTime, String runBy,
                            String runResult) {
        this.id = id;
        this.jobName = jobName;
        this.taskId = taskId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.runBy = runBy;
        this.runResult = runResult;
    }
}