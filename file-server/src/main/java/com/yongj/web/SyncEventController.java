package com.yongj.web;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.util.AsyncUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.vo.Result;
import com.yongj.config.EventSyncConfig;
import com.yongj.dao.FileInfo;
import com.yongj.file.remote.vo.FileInfoResp;
import com.yongj.helper.FileEventSyncSecretValidator;
import com.yongj.services.FileService;
import com.yongj.util.PathUtils;
import com.yongj.vo.FileEventVo;
import com.yongj.vo.sync.EventSyncReq;
import com.yongj.vo.sync.PollFileEventReq;
import com.yongj.vo.sync.SyncFileInfoReq;
import com.yongj.vo.sync.SyncFileInfoResp;
import com.yongj.web.streaming.ChannelStreamingResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.curtisnewbie.common.util.AssertUtils.notNull;

/**
 * Event Controller, mainly used for event synchronization
 * <p>
 * Since the backup shouldn't be saved on the same disk, it's possible that the file-service-follower is deployed
 * outside of the network, requesting through the gateway.
 *
 * @author yongj.zhuang
 */
@Slf4j
@RestController
@RequestMapping("${web.base-path}/sync")
public class SyncEventController {

    @Autowired
    private FileService fileService;
    @Autowired
    private FileEventSyncSecretValidator secretValidator;
    @Autowired
    private EventSyncConfig eventSyncConfig;

    /*
        curl -X POST http://localhost:8080/open/api/sync/event/poll -H 'content-type: application/json' -d '{ "secret" : "123456", "eventId": "0", "limit": "10" }'
     */
    @PostMapping("/event/poll")
    public DeferredResult<Result<List<FileEventVo>>> pollEvents(@RequestBody PollFileEventReq req, @Header("x-forwarded-for") String[] xForwardedFor,
                                                                HttpServletRequest hsr) {
        log.info("Received pollEvents request, req: {}", req);
        return AsyncUtils.runAsync(() -> {
            // validation
            final Result<?> err = preRequest(req, xForwardedFor, hsr.getRemoteAddr());
            if (err != null) return (Result<List<FileEventVo>>) err;

            if (req.getEventId() == null || req.getEventId() < 0L) req.setEventId(0L);
            if (req.getLimit() == null || req.getLimit() < 1 || req.getLimit() > 100) req.setLimit(100);
            return Result.of(fileService.fetchEventsAfter(req.getEventId(), req.getLimit()));
        });
    }

    /*
        curl -X POST http://localhost:8080/open/api/sync/file/info -H 'content-type: application/json' -d '{ "secret" : "123456", "fileKey": "e2e63cfd-a7fa-4b8a-9cb4-3a6f85991e3b" }'
     */
    @PostMapping("/file/info")
    public DeferredResult<Result<SyncFileInfoResp>> getSyncFileInfo(@RequestBody SyncFileInfoReq r, @Header("x-forwarded-for") String[] xForwardedFor,
                                                                    HttpServletRequest hsr) {
        log.info("Received getSyncFileInfo request, req: {}", r);
        return AsyncUtils.runAsync(() -> {
            // validation
            final Result<?> err = preRequest(r, xForwardedFor, hsr.getRemoteAddr());
            if (err != null) return (Result<SyncFileInfoResp>) err;

            final FileInfo f = fileService.findByKey(r.getFileKey());
            if (f == null) return Result.of(new SyncFileInfoResp());

            final FileInfoResp fir = BeanCopyUtils.toType(f, FileInfoResp.class);
            fir.setFileType(f.getFileType().name());
            return Result.of(new SyncFileInfoResp(fir));
        });
    }

    /*
        curl -X POST http://localhost:8080/open/api/sync/file/download -H 'content-type: application/json' -d '{ "secret" : "123456", "fileKey": "e2e63cfd-a7fa-4b8a-9cb4-3a6f85991e3b" }' -o temp.png
     */
    @PostMapping("/file/download")
    public StreamingResponseBody download(@RequestBody SyncFileInfoReq r, @Header("x-forwarded-for") String[] xForwardedFor, HttpServletResponse resp,
                                          HttpServletRequest hsr) {
        // validation
        final Result<?> err = preRequest(r, xForwardedFor, hsr.getRemoteAddr());
        if (err != null) err.assertIsOk();

        final FileInfo fi = fileService.findByKey(r.getFileKey());
        notNull(fi, "File not found or deleted");

        try {
            // set header for the downloaded file
            final String encoded = URLEncoder.encode(PathUtils.extractFileName(fi.getName()), StandardCharsets.UTF_8.name());
            resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encoded);
            resp.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fi.getSizeInBytes()));

            // use FileChannel#transferTo to download file
            return new ChannelStreamingResponseBody(fileService.retrieveFileChannel(fi.getId()), fi.getName());
        } catch (IOException e) {
            log.error("Download File (for event sync) failed", e);
            throw new UnrecoverableException("Failed to download file, unknown I/O exception occurred, " + e.getMessage());
        }
    }

    /** Validate the request, return error Result if validation fails, else return null */
    @Nullable
    private Result<?> preRequest(EventSyncReq r, String[] xForwardedFor, String remoteAddr) {
        notNull(r);
        if (!eventSyncConfig.isEnabled()) return Result.error("Event sync disabled");

        final String clientIp = (xForwardedFor == null || xForwardedFor.length < 1) ? remoteAddr : xForwardedFor[0];
        if (!eventSyncConfig.permitIpAddress(clientIp)) {
            log.warn("Received pollEvents request, ip address not permitted (clientIp: '{}'), request rejected", clientIp);
            return Result.error("Not permitted");
        }

        if (!secretValidator.validate(r.getSecret())) {
            log.warn("Received pollEvents request, but secret is invalid, request rejected");
            return Result.error("Invalid secret");
        }

        return null;
    }
}
