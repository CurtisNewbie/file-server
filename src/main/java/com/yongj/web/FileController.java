package com.yongj.web;

import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private IOHandler ioHandler;

    @Autowired
    private PathResolver pathResolver;

    @Value("${io.timeout}")
    private int readTimeOut;

    @PostMapping("/upload")
    public ResponseEntity<String> upload(String filePath, byte[] data) {
        if (!pathResolver.validateFileExtension(filePath)) {
            return ResponseEntity.badRequest().build();
        }
        String absPath = pathResolver.resolvePath(filePath);
        ioHandler.asyncWrite(absPath, data);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/download")
    public ResponseEntity<byte[]> download(String filePath) throws ExecutionException, InterruptedException, TimeoutException {
        if (!pathResolver.validateFileExtension(filePath)) {
            return ResponseEntity.badRequest().build();
        }
        String absPath = pathResolver.resolvePath(filePath);
        if (!ioHandler.exists(absPath))
            return ResponseEntity.notFound().build();

        Future<byte[]> result = ioHandler.asyncRead(absPath);
        byte[] bytes;
        if (readTimeOut == -1)
            bytes = result.get();
        else
            bytes = result.get(readTimeOut, TimeUnit.SECONDS);
        return ResponseEntity.ok(bytes);
    }
}
