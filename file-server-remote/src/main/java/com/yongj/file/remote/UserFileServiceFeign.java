package com.yongj.file.remote;

import com.curtisnewbie.common.vo.Result;
import com.yongj.file.remote.vo.FileInfoResp;
import com.yongj.file.remote.vo.GenFileTempTokenReq;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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
     * List file keys in dir
     *
     * @param fileKey fileKey (i.e., the uuid)
     * @param limit   limit (max: 100)
     * @param page    page (1-based index)
     */
    @GetMapping("/indir/list")
    Result<List<String>> listFilesInDir(@RequestParam("fileKey") String fileKey,
                                        @RequestParam("limit") long limit,
                                        @RequestParam("page") long page);

    /**
     * Get File info
     *
     * @param fileKey fileKey (i.e., the uuid)
     */
    @GetMapping("/info")
    Result<FileInfoResp> getFileInfo(@RequestParam("fileKey") String fileKey);

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

    /**
     * Generate temporary tokens that can be used to retrieve the file
     */
    @PostMapping("/temp/token")
    Result<Map<String, String>> generateFileTempToken(@RequestBody GenFileTempTokenReq req);

}
