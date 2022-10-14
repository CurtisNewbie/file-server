package com.yongj.job;

import com.curtisnewbie.common.data.*;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.task.scheduling.AbstractJob;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdReq;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdResp;
import com.yongj.services.FileService;
import com.yongj.vo.FileUploaderInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Job for fetching file's uploader name
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class FetchFileUploaderNameJob extends AbstractJob {

    private static final Integer LIMIT = 100;

    @Autowired
    private FileService fileInfoService;
    @Autowired
    private UserServiceFeign userServiceFeign;

    @Override
    public void executeInternal(TaskVo task) throws JobExecutionException {
        log.info("FetchFileUploaderNameJob started ...");

        LDTTimer.timedAndLogged(() -> {
            final long total = fetchUploaderName(fileInfoService.findFilesWithoutUploaderName(LIMIT));
            task.setLastRunResult(String.format("Fetched %s uploader names", total));
        }, "FetchFileUploaderNameJob");

        log.info("FetchFileUploaderNameJob finished ...");
    }

    /**
     * fetch uploaderName for each file
     */
    private long fetchUploaderName(final List<FileUploaderInfoVo> list) {
        List<Integer> uploaderIds = list.stream()
                .map(FileUploaderInfoVo::getUploaderId)
                .collect(Collectors.toList());

        IntWrapper count = new IntWrapper(0);
        Runner.runSafely(() -> {
                    final Result<FetchUsernameByIdResp> result = userServiceFeign.fetchUsernameById(FetchUsernameByIdReq.builder()
                            .userIds(uploaderIds)
                            .build());
                    if (!result.isOk()) {
                        log.error("Failed to fetch uploaderNames, err_msg: {}", result.getMsg());
                        return;
                    }

                    // update uploaderName to database
                    Map<Integer, String> idToUsername = result.getData().getIdToUsername();
                    list.forEach(file -> {
                        Optional.ofNullable(idToUsername.get(file.getUploaderId()))
                                .ifPresent(name -> {
                                    fileInfoService.fillBlankUploaderName(file.getId(), name);
                                    count.incr();
                                });
                    });
                },
                e -> log.error("Failed to fetch uploaderNames", e));

        return count.getValue();
    }

}
