package com.yongj.job;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@SpringBootTest
@Rollback
@Transactional
public class FetchFileUploaderNameJobTest {

    @Autowired
    private FetchFileUploaderNameJob job;

    @Test
    public void should_run() throws JobExecutionException {
        job.executeInternal(null);
    }


}
