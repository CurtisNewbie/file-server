package com.yongj.web;

import com.yongj.dto.Resp;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private IOHandler ioHandlerService;

    @Autowired
    private PathResolver pathResolver;

    @Value("${io.timeout}")
    private int readTimeOut;

    @PostMapping("/upload")
    public ResponseEntity<Resp<String>> upload(String filePath, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        pathResolver.validateFileExtension(filePath);
        String absPath = pathResolver.resolvePath(filePath);
        ioHandlerService.asyncWrite(absPath, multipartFile.getBytes());
        return ResponseEntity.ok(Resp.empty());
    }

    @PostMapping("/download")
    public ResponseEntity<Resp<byte[]>> download(String filePath) throws ExecutionException, InterruptedException, TimeoutException {
        pathResolver.validateFileExtension(filePath);
        String absPath = pathResolver.resolvePath(filePath);
        if (!ioHandlerService.exists(absPath))
            return ResponseEntity.ok(Resp.error("File not exists"));

        Future<byte[]> result = ioHandlerService.asyncRead(absPath);
        byte[] bytes;
        if (readTimeOut == -1)
            bytes = result.get();
        else
            bytes = result.get(readTimeOut, TimeUnit.SECONDS);
        return ResponseEntity.ok(Resp.of(bytes));
    }
}
