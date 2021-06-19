package com.yongj.job;

/**
 * Job that is scheduled by spring
 *
 * @author yongjie.zhuang
 */
public interface ScheduledJob {

    /**
     * Execution of job
     */
    void _exec();
}
