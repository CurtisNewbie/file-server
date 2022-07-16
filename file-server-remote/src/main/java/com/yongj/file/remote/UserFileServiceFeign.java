package com.yongj.file.remote;

import com.curtisnewbie.common.vo.Result;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * File Service Feign (for user file)
 *
 * @author yongjie.zhuang
 */
@FeignClient(
        value = AppConst.FILE_SERVICE,
        path = UserFileServiceFeign.PATH
)
public interface UserFileServiceFeign {

    String PATH = "/remote/user/file";

    /**
     * Check whether the user is actually the file's owner
     *
     * @param fileKey fileKey (i.e., the uuid)
     * @param userId  user's id (not userNo)
     * @return is the file's owner
     */
    @GetMapping("/owner/validation")
    Result<Boolean> checkFileOwner(@RequestParam("fileKey") String fileKey, @RequestParam("userId") int userId);

    /**
     * Download user file
     *
     * @param fileKey file's key
     * @return response
     */
    @GetMapping("/download")
    Response download(@RequestParam("fileKey") String fileKey);

}
