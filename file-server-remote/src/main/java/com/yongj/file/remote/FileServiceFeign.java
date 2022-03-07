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

import java.io.IOException;

/**
 * File Service Feign
 *
 * @author yongjie.zhuang
 */
@FeignClient(
        value = AppConst.FILE_SERVICE,
        path = FileServiceFeign.PATH
)
public interface FileServiceFeign {

    String PATH = "/remote/file";

    /**
     * Upload file
     * <p>
     * Used by app only, not for users
     * </p>
     *
     * @param fileName      name of the file
     * @param multipartFile multipart file
     * @param appName       app's name
     * @return file's key
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    Result<String> uploadAppFile(@RequestParam("fileName") String fileName, @RequestPart("file") MultipartFile multipartFile,
                                 @RequestParam("app") String appName) throws IOException;


    /**
     * Download file
     *
     * @param fileKey file's key
     * @return response
     */
    @GetMapping("/download")
    Response download(@RequestParam("fileKey") String fileKey) throws IOException;

}
