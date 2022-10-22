package com.yongj.job;

import com.curtisnewbie.module.task.vo.TaskVo;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DeleteFileJobTest {

    @Autowired
    private DeleteFileJob job;

    @Test
    public void should_run_job() throws JobExecutionException {
        TaskVo task = new TaskVo();
        job.executeInternal(task);
    }

}