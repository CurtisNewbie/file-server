package com.yongj.remote;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.vo.Result;
import com.yongj.dao.FileInfo;
import com.yongj.file.remote.UserFileServiceFeign;
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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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

    @GetMapping("/owner/validation")
    public Result<Boolean> checkFileOwner(@RequestParam("fileKey") String fileKey, @RequestParam("userId") int userId) {
        return Result.of(fileService.isFileOwner(userId, fileKey));
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
