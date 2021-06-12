package com.yongj.web;

import com.curtisnewbie.module.auth.util.AuthUtil;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.ParamInvalidException;
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
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<?>> upload(@RequestParam("fileName") String fileName,
                                          @RequestParam("file") MultipartFile multipartFile,
                                          @RequestParam("userGroup") Integer userGroup) throws IOException {
        pathResolver.validateFileExtension(fileName);
        FileUserGroupEnum userGroupEnum = FileUserGroupEnum.parseGroup(userGroup);
        if (userGroupEnum == null) {
            return ResponseEntity.ok(Resp.error("Incorrect user group"));
        }
        fileInfoService.uploadFile(AuthUtil.getUserId(), fileName, userGroupEnum, multipartFile.getInputStream());
        return ResponseEntity.ok(Resp.ok());
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@PathParam("uuid") String uuid, HttpServletResponse resp) throws IOException, ParamInvalidException {
        final int userId = AuthUtil.getUserId();
        // validate user authority
        fileInfoService.validateUserDownload(userId, uuid);
        // get fileName
        final String filename = fileInfoService.getFilename(uuid);
        // set header for the downloaded file
        resp.setHeader("Content-Disposition", "attachment; filename=" + encodeAttachmentName(filename));
        // transfer file using nio
        fileInfoService.downloadFile(uuid, resp.getOutputStream());
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
