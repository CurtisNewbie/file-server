package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.aop.LogOperation;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.github.pagehelper.PageInfo;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileExtensionIsEnabledEnum;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.exceptions.NoWritableFsGroupException;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileInfoService;
import com.yongj.services.TempTokenFileDownloadService;
import com.yongj.util.PathUtils;
import com.yongj.vo.*;
import com.yongj.web.streaming.GzipStreamingResponseBody;
import com.yongj.web.streaming.PlainStreamingResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/file")
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
    @Autowired
    private TempTokenFileDownloadService tempTokenFileDownloadService;

    @LogOperation(name = "/file/upload", description = "upload file")
    @PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> upload(@RequestParam("fileName") String[] fileNames,
                            @RequestParam("file") MultipartFile[] multipartFiles,
                            @RequestParam("userGroup") Integer userGroup) throws IOException, InvalidAuthenticationException,
            MsgEmbeddedException {

        FileUserGroupEnum userGroupEnum = FileUserGroupEnum.parse(userGroup);
        ValidUtils.requireNonNull(userGroupEnum, "Incorrect user group");
        ValidUtils.requireNotEmpty(multipartFiles, "No file uploaded");
        ValidUtils.requireNotEmpty(fileNames, "No file uploaded");

        // only validate the first fileName, if there is only one file, this will be the name of the file
        // if there are multiple files, this will be the name of the zip file
        pathResolver.validateFileExtension(fileNames[0]);

        try {
            if (multipartFiles.length == 1) {
                fileInfoService.uploadFile(AuthUtil.getUserId(), fileNames[0], userGroupEnum, multipartFiles[0].getInputStream());
            } else { // multiple upload, compress them into a single file zip file
                // the first one is the zipFile's name, and the rest are the entries
                if (fileNames.length != multipartFiles.length + 1)
                    throw new MsgEmbeddedException("Parameters illegal");

                String zipFile = fileNames[0];
                String[] entryNames = Arrays.copyOfRange(fileNames, 1, fileNames.length);
                fileInfoService.uploadFilesAsZip(AuthUtil.getUserId(), zipFile, entryNames, userGroupEnum, collectInputStreams(multipartFiles));
            }
        } catch (NoWritableFsGroupException e) {
            return Result.error("No writable fs_group found, unable to upload file, please contact administrator");
        }
        return Result.ok();
    }

    @LogOperation(name = "/file/download", description = "download file")
    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody download(@PathParam("uuid") String uuid, HttpServletResponse resp, HttpServletRequest req)
            throws MsgEmbeddedException, InvalidAuthenticationException, IOException {

        final int userId = AuthUtil.getUserId();

        // validate user authority
        fileInfoService.validateUserDownload(userId, uuid);

        // get fileInfo
        final FileInfo fi = fileInfoService.findByUuid(uuid);
        ValidUtils.requireNonNull(fi, "File not found");

        return download(req, resp, fi);
    }

    @LogOperation(name = "/file/list", description = "list file")
    @PostMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileInfoRespVo> listAll(@RequestBody ListFileInfoReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {
        ValidUtils.requireNonNull(reqVo.getPagingVo());
        ValidUtils.requireNonNull(reqVo.getPagingVo().getLimit());
        ValidUtils.requireNonNull(reqVo.getPagingVo().getPage());
        reqVo.setUserId(AuthUtil.getUserId());
        PageInfo<FileInfoVo> fileInfoVoPageInfo = fileInfoService.findPagedFilesForUser(reqVo);
        ListFileInfoRespVo res = new ListFileInfoRespVo(fileInfoVoPageInfo.getList());
        res.setPagingVo(new PagingVo().ofTotal(fileInfoVoPageInfo.getTotal()));
        return Result.of(res);
    }

    @LogOperation(name = "/file/delete", description = "delete file")
    @PostMapping(path = "/delete")
    public Result<Void> deleteFile(@RequestBody LogicDeleteFileReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {
        ValidUtils.requireNonNull(reqVo.getUuid());
        fileInfoService.deleteFileLogically(AuthUtil.getUserId(), reqVo.getUuid());
        return Result.ok();
    }

    @LogOperation(name = "/file/extension/name", description = "list supported file extensions")
    @GetMapping(path = "/extension/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<String>> listSupportedFileExtensionNames() {
        return Result.of(
                fileExtensionService.getNamesOfAllEnabled()
        );
    }

    @LogOperation(name = "/file/extension/add", description = "add file extension")
    @PostMapping("/extension/add")
    public Result<Void> addFileExtension(@RequestBody AddFileExtReqVo reqVo) throws MsgEmbeddedException {
        ValidUtils.requireNotEmpty(reqVo.getName());
        FileExtension ext = new FileExtension();
        // by default disabled
        ext.setIsEnabled(FileExtensionIsEnabledEnum.DISABLED.getValue());
        ext.setName(reqVo.getName());
        fileExtensionService.addFileExt(ext);
        return Result.ok();
    }

    @LogOperation(name = "/file/extension/list", description = "list supported file extension details")
    @PostMapping(path = "/extension/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileExtRespVo> listSupportedFileExtensionDetails(@RequestBody ListFileExtReqVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getPagingVo());
        PageInfo<FileExtVo> pageInfo = fileExtensionService.getDetailsOfAllByPageSelective(vo);
        ListFileExtRespVo res = new ListFileExtRespVo(pageInfo.getList());
        res.setPagingVo(new PagingVo().ofTotal(pageInfo.getTotal()));
        return Result.of(res);
    }

    @LogOperation(name = "/file/extension/update", description = "update status of supported file extension")
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping(path = "/extension/update")
    public Result<Void> updateFileExtensionStatus(@RequestBody FileExtVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getId());
        // either the name or isEnabled should be entered
        if (vo.getIsEnabled() == null && !StringUtils.hasText(vo.getName())) {
            throw new MsgEmbeddedException("Required parameters should not be null");
        }
        // check if the isEnabled value is valid
        FileExtensionIsEnabledEnum isEnabledEnum = EnumUtils.parse(vo.getIsEnabled(),
                FileExtensionIsEnabledEnum.class);
        ValidUtils.requireNonNull(isEnabledEnum);
        fileExtensionService.updateFileExtSelective(vo);
        return Result.ok();
    }

    @LogOperation(name = "/file/usergroup/update", description = "update file's user group")
    @PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
    @PostMapping("/usergroup/update")
    public Result<Void> updateFileUserGroup(@RequestBody UpdateFileUserGroupReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {
        ValidUtils.requireNotEmpty(reqVo.getUuid(), "UUID can't be null");
        ValidUtils.requireNonNull(reqVo.getUserGroup(), "UserGroup can't be null");

        FileUserGroupEnum fug = EnumUtils.parse(reqVo.getUserGroup(), FileUserGroupEnum.class);
        ValidUtils.requireNonNull(fug, "Illegal UserGroup value");

        fileInfoService.updateFileUserGroup(reqVo.getUuid(), fug, AuthUtil.getUserId());
        return Result.ok();
    }

    @LogOperation(name = "/file/token/generate", description = "generate temp token for file download")
    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @PostMapping("/token/generate")
    public Result<String> generateToken(@RequestBody GenerateTokenReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException, NoSuchMethodException {
        ValidUtils.requireNotEmpty(reqVo.getUuid(), "UUID can't be empty");
        FileInfo fi = fileInfoService.findByUuid(reqVo.getUuid());
        ValidUtils.requireNonNull(fi, "File not found");

        if (!Objects.equals(fi.getUploaderId(), AuthUtil.getUserId())) {
            throw new MsgEmbeddedException("Only the owner of the file can generate temporary token");
        }

        final String token = tempTokenFileDownloadService.generateTempTokenForFile(reqVo.getUuid());
        String link = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/file/token/download")
                .query("token={token}")
                .buildAndExpand(token)
                .toUri().toString();
        return Result.of(link);
    }

    @LogOperation(name = "/file/token/download", description = "Download file using temp token")
    @GetMapping("/token/download")
    public StreamingResponseBody downloadByToken(HttpServletRequest req, HttpServletResponse resp,
                                                 @PathParam("token") String token) throws IOException,
            MsgEmbeddedException, InvalidAuthenticationException {

        logger.info("User {} attempts to download file using token {}", AuthUtil.getUsername(), token);

        ValidUtils.requireNotEmpty(token, "Token can't be empty");
        final String uuid = tempTokenFileDownloadService.getUuidByToken(token);
        ValidUtils.requireNonNull(uuid, "Token is invalid or expired");

        FileInfo fi = fileInfoService.findByUuid(uuid);
        ValidUtils.requireNonNull(fi, "File not found");
        if (!Objects.equals(fi.getIsLogicDeleted(), FileLogicDeletedEnum.NORMAL.getValue())) {
            // remove the token
            tempTokenFileDownloadService.removeToken(token);
            throw new MsgEmbeddedException("File is deleted already");
        }

        return download(req, resp, fi);
    }


    private StreamingResponseBody download(HttpServletRequest req, HttpServletResponse resp, FileInfo fi) throws IOException {
        // set header for the downloaded file
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodeAttachmentName(fi.getName()));
        resp.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fi.getSizeInBytes()));

        // negotiate whether we should use gzip or plain streaming
        Enumeration<String> encodings = req.getHeaders(HttpHeaders.ACCEPT_ENCODING);
        boolean useGzip = false;
        while (encodings.hasMoreElements()) {
            if (encodings.nextElement().trim().equalsIgnoreCase("gzip"))
                useGzip = true;
        }

        // write file directly to outputStream without holding servlet's thread
        InputStream in = fileInfoService.retrieveFileInputStream(fi.getUuid());
        if (useGzip) {
            resp.addHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
            return new GzipStreamingResponseBody(in);
        } else
            return new PlainStreamingResponseBody(in);
    }

    private static String encodeAttachmentName(String filePath) {
        return URLEncoder.encode(PathUtils.extractFileName(filePath), StandardCharsets.UTF_8);
    }

    private static InputStream[] collectInputStreams(MultipartFile[] files) throws IOException {
        InputStream[] inputStreams = new InputStream[files.length];
        for (int i = 0; i < files.length; i++)
            inputStreams[i] = files[i].getInputStream();
        return inputStreams;
    }
}
