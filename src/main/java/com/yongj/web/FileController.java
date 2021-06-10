package com.yongj.web;

import com.curtisnewbie.module.auth.util.AuthUtil;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileInfoService;
import com.yongj.util.PathUtils;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.Resp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    private FileExtensionService fileExtensionService;
    @Autowired
    private FileInfoService fileInfoService;

    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<?>> upload(@RequestParam("fileName") String fileName,
                                          @RequestParam("file") MultipartFile multipartFile,
                                          @RequestParam("userGroup") String userGroup) throws IOException {
        pathResolver.validateFileExtension(fileName);
        fileName = pathResolver.validatePath(fileName);
        fileInfoService.saveFileInfo(AuthUtil.getUserId(), fileName, userGroup, multipartFile.getInputStream());
        return ResponseEntity.ok(Resp.ok());
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathParam("uuid") String uuid, HttpServletResponse resp) throws IOException {
//        String absPath = pathResolver.resolvePath(filePath);
//        if (!ioHandler.exists(absPath)) {
//            resp.setStatus(HttpStatus.NOT_FOUND.value());
//            return;
//        }

        String filePath = "demo";
        // set header for the downloaded file
        resp.setHeader("Content-Disposition", "attachment; filename=" + encodeAttachmentName(filePath));
        // transfer file using nio
        fileInfoService.downloadFile(uuid, resp.getOutputStream());
//        ioHandler.readByChannel(absPath, resp.getOutputStream());
    }

    @GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<Iterable<FileInfoVo>>> listAll() {
        return ResponseEntity.ok(Resp.of(fileInfoService.findFilesForUser(AuthUtil.getUserId())));
    }

    @GetMapping(path = "/extension", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<List<String>>> listSupportedFileExtension() {
        return ResponseEntity.ok(Resp.of(
                fileExtensionService.getNamesOfAllEnabled()
        ));
    }

    private static final String encodeAttachmentName(String filePath) {
        return URLEncoder.encode(PathUtils.extractFileName(filePath), StandardCharsets.UTF_8);
    }
}
