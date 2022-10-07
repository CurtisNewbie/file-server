package com.yongj.job;

import com.curtisnewbie.common.vo.*;
import com.curtisnewbie.service.auth.remote.feign.*;
import com.curtisnewbie.service.auth.remote.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
    @Autowired
    private UserServiceFeign userServiceFeign;

    @Test
    public void should_run() throws JobExecutionException {
        job.executeInternal(null);
    }

    @Test
    void should_fetch_username_by_id() {
        final Result<FetchUsernameByIdResp> resp = userServiceFeign.fetchUsernameById(FetchUsernameByIdReq
                .builder()
                .userIds(Arrays.asList(1))
                .build());
        log.info("Resp: {}", resp);
    }


}
