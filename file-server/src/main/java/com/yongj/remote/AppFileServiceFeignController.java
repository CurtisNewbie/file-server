package com.yongj.remote;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.vo.Result;
import com.yongj.file.remote.AppFileServiceFeign;
import com.yongj.services.AppFileService;
import com.yongj.vo.UploadAppFileCmd;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@RequestMapping(value = AppFileServiceFeign.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class AppFileServiceFeignController implements AppFileServiceFeign {

    @Autowired
    private AppFileService appFileService;

    @Override
    public Result<String> uploadAppFile(MultipartFile multipartFile,
                                        String appName,
                                        Integer userId) {
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

    @Override
    public Response download(String fileKey) {
        try {
            return appFileService.download(fileKey);
        } catch (IOException e) {
            log.error("Download AppFile failed", e);
            throw new UnrecoverableException("Failed to download file, unknown I/O exception occurred, " + e.getMessage());
        }
    }
}
