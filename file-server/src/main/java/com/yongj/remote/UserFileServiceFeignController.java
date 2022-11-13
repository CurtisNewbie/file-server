package com.yongj.remote;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.AsyncUtils;
import com.curtisnewbie.common.util.ValueUtils;
import com.curtisnewbie.common.vo.Result;
import com.yongj.dao.FileInfo;
import com.yongj.file.remote.UserFileServiceFeign;
import com.yongj.file.remote.vo.FileInfoResp;
import com.yongj.services.FileService;
import com.yongj.util.PathUtils;
import com.yongj.web.streaming.ChannelStreamingResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static com.curtisnewbie.common.util.AssertUtils.notNull;

/**
 * @author yongjie.zhuang
 * @see com.yongj.file.remote.UserFileServiceFeign
 */
@Slf4j
@RequestMapping(value = UserFileServiceFeign.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class UserFileServiceFeignController {

    @Autowired
    private FileService fileService;

    // curl "http://localhost:8080/remote/user/file/indir/list?fileKey=5ddf49ca-dec9-4ecf-962d-47b0f3eab90c&limit=10&page=1"
    @GetMapping("/indir/list")
    public DeferredResult<Result<List<String>>> listFilesInDir(@RequestParam("fileKey") String fileKey,
                                                               @RequestParam("limit") long limit,
                                                               @RequestParam("page") long page) {
        // 1~100
        if (!ValueUtils.inBetween(limit, 1, 100)) {
            limit = 100;
        }
        // 1-based page number
        if (page < 1) page = 1;

        final long flimit = limit;
        final long fpage = page;

        return AsyncUtils.runAsyncResult(() -> {
            List<String> fileKeys = fileService.listFilesInDir(fileKey, flimit, fpage);
            if (fileKeys == null) fileKeys = Collections.emptyList();
            return fileKeys;
        });
    }

    // curl "http://localhost:8080/remote/user/file/info?fileKey=5ddf49ca-dec9-4ecf-962d-47b0f3eab90c"
    @GetMapping("/info")
    public DeferredResult<Result<FileInfoResp>> getFileInfo(@RequestParam("fileKey") String fileKey) {
        return AsyncUtils.runAsyncResult(() -> {
            final FileInfoResp f = fileService.findRespByKey(fileKey);
            AssertUtils.notNull(f, "File not found");
            return f;
        });
    }

    @GetMapping("/owner/validation")
    public DeferredResult<Result<Boolean>> checkFileOwner(@RequestParam("fileKey") String fileKey, @RequestParam("userId") int userId) {
        return AsyncUtils.runAsyncResult(() -> fileService.isFileOwner(userId, fileKey));
    }

    @GetMapping("/download")
    public StreamingResponseBody download(@RequestParam("fileKey") String fileKey, HttpServletResponse resp) {
        try {
            final FileInfo fi = fileService.findByKey(fileKey);
            notNull(fi, "File not found");

            final String name = fi.getName();

            // set header for the downloaded file
            final String encoded = URLEncoder.encode(PathUtils.extractFileName(name), StandardCharsets.UTF_8.name());
            resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encoded);
            resp.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fi.getSizeInBytes()));

            // use FileChannel#transferTo to download file
            return new ChannelStreamingResponseBody(fileService.retrieveFileChannel(fi.getId()), fi.getName());

        } catch (IOException e) {
            log.error("Download App File failed", e);
            throw new UnrecoverableException("Failed to download file, unknown I/O exception occurred, " + e.getMessage());
        }
    }
}
