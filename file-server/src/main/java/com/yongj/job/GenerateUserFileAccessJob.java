package com.yongj.job;

import com.curtisnewbie.common.util.LDTTimer;
import com.curtisnewbie.module.task.annotation.JobDeclaration;
import com.curtisnewbie.module.task.scheduling.AbstractJob;
import com.curtisnewbie.module.task.vo.TaskVo;
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
@JobDeclaration(name = "Job that generates user access to the files", cron = "0 0 0 ? * *")
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
