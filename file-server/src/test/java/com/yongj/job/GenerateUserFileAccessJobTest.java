package com.yongj.job;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class GenerateUserFileAccessJobTest {

    @Autowired
    private GenerateUserFileAccessJob job;

    @Test
    public void should_run_job() throws JobExecutionException {
        job.executeInternal(null);
    }
}