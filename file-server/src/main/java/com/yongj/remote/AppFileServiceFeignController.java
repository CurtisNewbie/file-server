package com.yongj.remote;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.vo.Result;
import com.yongj.file.remote.AppFileServiceFeign;
import com.yongj.services.AppFileService;
import com.yongj.util.PathUtils;
import com.yongj.vo.AppFileDownloadInfo;
import com.yongj.vo.UploadAppFileCmd;
import com.yongj.web.streaming.ChannelStreamingResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author yongjie.zhuang
 * @see AppFileServiceFeign
 */
@Slf4j
@RequestMapping(value = AppFileServiceFeign.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AppFileServiceFeignController {

    @Autowired
    private AppFileService appFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<String> uploadAppFile(@RequestPart("file") MultipartFile multipartFile,
                                        @RequestParam("app") String appName,
                                        @RequestParam(value = "userId", required = false) Integer userId) {
        try {
            final UploadAppFileCmd cmd = UploadAppFileCmd.builder()
                    .userId(userId)
                    .fileName(multipartFile.getName())
                    .inputStream(multipartFile.getInputStream())
                    .appName(appName)
                    .build();
            final String uuid = appFileService.upload(cmd);

            return Result.of(uuid);
        } catch (IOException e) {
            log.error("Upload AppFile failed", e);
            return Result.error("Failed to upload file, unknown I/O exception occurred, " + e.getMessage());
        }
    }

    @GetMapping("/download")
    public StreamingResponseBody download(@RequestParam("fileKey") String fileKey, HttpServletResponse resp) {
        try {
            final AppFileDownloadInfo downloadInfo = appFileService.download(fileKey);
            final String name = downloadInfo.getName();

            // set header for the downloaded file
            final String encoded = URLEncoder.encode(PathUtils.extractFileName(name), StandardCharsets.UTF_8.name());
            resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encoded);
            resp.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(downloadInfo.getSize()));

            // use FileChannel#transferTo to download file
            return new ChannelStreamingResponseBody(downloadInfo.getFileChannel(), downloadInfo.getName());

        } catch (IOException e) {
            log.error("Download App File failed", e);
            throw new UnrecoverableException("Failed to download file, unknown I/O exception occurred, " + e.getMessage());
        }
    }
}
