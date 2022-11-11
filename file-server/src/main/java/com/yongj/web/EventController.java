package com.yongj.web;

import com.curtisnewbie.common.util.AsyncUtils;
import com.curtisnewbie.common.vo.Result;
import com.yongj.helper.FileEventSyncSecretValidator;
import com.yongj.services.FileService;
import com.yongj.vo.FileEventVo;
import com.yongj.vo.PollFileEventReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * Event Controller, mainly used for event synchronization
 *
 * @author yongj.zhuang
 */
@Slf4j
@RestController
@RequestMapping("${web.base-path}/event")
public class EventController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileEventSyncSecretValidator secretValidator;

    // TODO impl an endpoint to list all the files with the secret?
    // TODO impl an endpoint to download the file with the secret?

    /*
        curl -X POST http://localhost:8080/open/api/event/poll -H 'content-type: application/json' -d '{ "secret" : "123456", "eventId": "0", "limit": "10" }'
     */
    @PostMapping("/poll")
    public DeferredResult<Result<List<FileEventVo>>> pollEvents(@RequestBody PollFileEventReq req) {
        log.info("Received pollEvents request, req: {}", req);
        return AsyncUtils.runAsync(() -> {
            if (!secretValidator.validate(req.getSecret())) {
                log.warn("Received pollEvents request, but secret is invalid, request rejected");
                return Result.error("invalid secret");
            }
            if (req.getEventId() == null || req.getEventId() < 0L) req.setEventId(0L);
            if (req.getLimit() == null || req.getLimit() < 1 || req.getLimit() > 100) req.setLimit(100);
            return Result.of(fileService.fetchEventsAfter(req.getEventId(), req.getLimit()));
        });
    }
}
