package com.yongj.web;

import com.yongj.dto.Resp;
import com.yongj.io.api.FileManager;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import com.yongj.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
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

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private IOHandler ioHandlerService;

    @Autowired
    private PathResolver pathResolver;

    @Autowired
    private FileManager fileManager;

    @Value("${io.timeout}")
    private int readTimeOut;

    @PostConstruct
    void init() {
        if (readTimeOut >= 0)
            logger.info("[INIT] Setting timeout '{}' seconds for IOHandler's operations", readTimeOut);
        else
            logger.info("[INIT] Setting no timeout for IOHandler's operations");
    }

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<?>> upload(@RequestParam("filePath") String filePath, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        pathResolver.validateFileExtension(filePath);
        String absPath = pathResolver.resolvePath(pathResolver.validatePath(filePath));
        ioHandlerService.asyncWrite(absPath, multipartFile.getBytes());
        fileManager.cache(pathResolver.relativizePath(absPath));
        return ResponseEntity.ok(Resp.ok());
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> download(@PathParam("filePath") String filePath) throws ExecutionException, InterruptedException, TimeoutException {
        String absPath = pathResolver.resolvePath(filePath);
        if (!ioHandlerService.exists(absPath))
            return ResponseEntity.notFound().build();

        Future<byte[]> result = ioHandlerService.asyncRead(absPath);
        byte[] bytes;
        if (readTimeOut >= 0)
            bytes = result.get(readTimeOut, TimeUnit.SECONDS);
        else
            bytes = result.get();
        ResponseEntity respEntity = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + URLEncoder.encode(PathUtils.extractFileName(filePath), StandardCharsets.UTF_8))
                .contentLength(bytes.length)
                .body(bytes);
        return respEntity;
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<Iterable<String>>> listAll() {
        return ResponseEntity.ok(Resp.of(fileManager.getAll()));
    }

    @GetMapping(path = "/extension", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<List<String>>> listSupportedFileExtension() {
        return ResponseEntity.ok(Resp.of(pathResolver.getSupportedFileExtension()));
    }
}
