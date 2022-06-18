package com.yongj.file.remote;

import com.curtisnewbie.common.vo.Result;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Service Feign
 *
 * @author yongjie.zhuang
 */
@FeignClient(
        value = AppConst.FILE_SERVICE,
        path = AppFileServiceFeign.PATH
)
public interface AppFileServiceFeign {

    String PATH = "/remote/app/file";

    /**
     * Upload file
     * <p>
     * Used by app only, not for users
     * </p>
     *
     * @param multipartFile multipart file
     * @param appName       app's name
     * @return file's key
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> uploadAppFile(@RequestPart("file") MultipartFile multipartFile,
                                 @RequestParam("app") String appName,
                                 @RequestParam(value = "userId", required = false) Integer userId);


    /**
     * Download file
     *
     * @param fileKey file's key
     * @return response
     */
    @GetMapping("/download")
    Response download(@RequestParam("fileKey") String fileKey);

}
