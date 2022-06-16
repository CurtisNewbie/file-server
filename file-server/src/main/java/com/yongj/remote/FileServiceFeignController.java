package com.yongj.remote;

import com.curtisnewbie.common.vo.Result;
import com.yongj.file.remote.FileServiceFeign;
import com.yongj.services.FileService;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yongjie.zhuang
 */
@RequestMapping(value = FileServiceFeign.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
public class FileServiceFeignController implements FileServiceFeign {

    @Autowired
    private FileService fileService;

    @Override
    public Result<String> uploadAppFile(String fileName, MultipartFile multipartFile, String appName) {
//        final FileInfo fi = fileService.uploadAppFile(UploadAppFileCmd.builder()
//                .fileName(fileName)
//                .inputStream(multipartFile.getInputStream())
//                .uploadApp(appName)
//                .build());

        // todo to be implemented
        return Result.of("");
    }

    @Override
    public Response download(String fileKey) {
//        return Response.builder()
//                .body(fileService.retrieveFileInputStream(fileKey), null)
//                .build();

        // todo to be implemented
        return null;
    }
}
