package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * Vo for task
 *
 * @author yongjie.zhuang
 */
@Data
public class TaskFsVo {

    /** id */
    private Integer id;

    /** job's name */
    private String jobName;

    /** name of bean that will be executed */
    private String targetBean;

    /** cron expression */
    private String cronExpr;

    /** app group that runs this task */
    private String appGroup;

    /** the last time this task was executed */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private Date lastRunStartTime;

    /** the last time this task was finished */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private Date lastRunEndTime;

    /** app that previously ran this task */
    private String lastRunBy;

    /** result of last execution */
    private String lastRunResult;

    /** whether the task is enabled: 0-disabled, 1-enabled */
    private Integer enabled;

    /** whether the task can be executed concurrently: 0-disabled, 1-enabled */
    private Integer concurrentEnabled;

    /** update date */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private Date updateDate;

    /** updated by */
    private String updateBy;

}