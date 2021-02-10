package com.yongj.web;

import com.yongj.dto.Resp;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
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
@CrossOrigin // TODO remove this
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private IOHandler ioHandlerService;

    @Autowired
    private PathResolver pathResolver;

    @Value("${io.timeout}")
    private int readTimeOut;

    @PostConstruct
    void init() {
        logger.info("[INIT] Setting timeout '{}' seconds for IOHandler's operations", readTimeOut);
    }

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<?>> upload(@RequestParam("filePath") String filePath, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        pathResolver.validateFileExtension(filePath);
        String absPath = pathResolver.resolvePath(filePath);
        ioHandlerService.asyncWrite(absPath, multipartFile.getBytes());
        return ResponseEntity.ok(Resp.ok());
    }

    @PostMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resp<byte[]>> download(@RequestParam("filePath") String filePath) throws ExecutionException, InterruptedException, TimeoutException {
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
