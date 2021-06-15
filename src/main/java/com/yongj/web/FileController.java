package com.yongj.web;

import com.curtisnewbie.module.auth.util.AuthUtil;
import com.github.pagehelper.PageInfo;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.ParamInvalidException;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileInfoService;
import com.yongj.util.PathUtils;
import com.yongj.util.ValidUtils;
import com.yongj.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
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
    private static final int BUFFER_SIZE = 8192;

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
        FileUserGroupEnum userGroupEnum = FileUserGroupEnum.parse(userGroup);
        if (userGroupEnum == null) {
            return ResponseEntity.ok(Resp.error("Incorrect user group"));
        }
        fileInfoService.uploadFile(AuthUtil.getUserId(), fileName, userGroupEnum, multipartFile.getInputStream());
        return ResponseEntity.ok(Resp.ok());
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody download(@PathParam("uuid") String uuid, HttpServletResponse resp) throws ParamInvalidException {
        final int userId = AuthUtil.getUserId();
        // validate user authority
        fileInfoService.validateUserDownload(userId, uuid);
        // get fileName
        final String filename = fileInfoService.getFilename(uuid);
        // set header for the downloaded file
        resp.setHeader("Content-Disposition", "attachment; filename=" + encodeAttachmentName(filename));
        // write file directly to outputStream without holdingup servlet's thread
        return outputStream -> {
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            InputStream inputStream = fileInfoService.retrieveFileInputStream(uuid);
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        };
    }

    @PostMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resp<ListFileInfoRespVo>> listAll(@RequestBody ListFileInfoReqVo reqVo) throws ParamInvalidException {
        ValidUtils.requireNonNull(reqVo.getPagingVo());
        ValidUtils.requireNonNull(reqVo.getPagingVo().getLimit());
        ValidUtils.requireNonNull(reqVo.getPagingVo().getPage());
        reqVo.setUserId(AuthUtil.getUserId());
        PageInfo<FileInfoVo> fileInfoVoPageInfo = fileInfoService.findPagedFilesForUser(reqVo);
        PagingVo paging = new PagingVo();
        paging.setTotal(fileInfoVoPageInfo.getTotal());
        return ResponseEntity.ok(Resp.of(new ListFileInfoRespVo(fileInfoVoPageInfo.getList(), paging)));
    }

    @PostMapping(path = "/delete")
    public Resp<Void> deleteFile(@RequestBody DeleteFileReqVo reqVo) throws ParamInvalidException {
        ValidUtils.requireNonNull(reqVo.getUuid());
        fileInfoService.deleteFileLogically(AuthUtil.getUserId(), reqVo.getUuid());
        return Resp.ok();
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
