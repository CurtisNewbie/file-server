package com.yongj.job;

import com.curtisnewbie.common.util.LDTTimer;
import com.curtisnewbie.module.task.scheduling.*;
import com.curtisnewbie.module.task.vo.*;
import com.yongj.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Job for generating user's file access
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class GenerateUserFileAccessJob extends AbstractJob {

    @Autowired
    private FileService fileInfoService;

    @Override
    public void executeInternal(TaskVo task) throws JobExecutionException {
        LDTTimer timer = LDTTimer.startTimer();
        log.info("GenerateUserFileAccessJob started");
        fileInfoService.loadUserFileAccess();
        log.info("GenerateUserFileAccessJob finished, took: {}", timer.stop().printDuration());
        if (task != null) task.setLastRunResult("Success");
    }

}
