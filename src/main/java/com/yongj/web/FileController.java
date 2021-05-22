package com.yongj.web;

import com.yongj.dao.FileExtensionMapper;
import com.yongj.dto.FileInfo;
import com.yongj.dto.Resp;
import com.yongj.io.api.FileManager;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import com.yongj.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private IOHandler ioHandler;

    @Autowired
    private PathResolver pathResolver;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private FileExtensionMapper fileExtensionMapper;

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<?>> upload(@RequestParam("filePath") String filePath, @RequestParam("file") MultipartFile multipartFile) throws IOException {
        pathResolver.validateFileExtension(filePath);
        String absPath = pathResolver.resolvePath(pathResolver.validatePath(filePath));
        // Use channel by default
        ioHandler.writeByChannel(absPath, multipartFile.getInputStream());
        fileManager.cache(pathResolver.relativizePath(absPath));
        return ResponseEntity.ok(Resp.ok());
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathParam("filePath") String filePath, HttpServletResponse resp) throws IOException {
        String absPath = pathResolver.resolvePath(filePath);
        if (!ioHandler.exists(absPath)) {
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }

        // set header for the downloaded file
        resp.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(PathUtils.extractFileName(filePath), StandardCharsets.UTF_8));
        // transfer file using nio
        ioHandler.readByChannel(absPath, resp.getOutputStream());
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<Iterable<FileInfo>>> listAll() {
        return ResponseEntity.ok(Resp.of(fileManager.getAll()));
    }

    @GetMapping(path = "/extension", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<List<String>>> listSupportedFileExtension() {
        return ResponseEntity.ok(Resp.of(
                fileExtensionMapper.findNamesOfAllEnabled()
        ));
    }
}
