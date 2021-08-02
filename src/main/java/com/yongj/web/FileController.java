package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.github.pagehelper.PageInfo;
import com.yongj.enums.FileExtensionIsEnabledEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileInfoService;
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

    @PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> upload(@RequestParam("fileName") String[] fileNames,
                            @RequestParam("file") MultipartFile[] multipartFiles,
                            @RequestParam("userGroup") Integer userGroup) throws IOException, InvalidAuthenticationException, MsgEmbeddedException {
        for (String f : fileNames)
            pathResolver.validateFileExtension(f);
        FileUserGroupEnum userGroupEnum = FileUserGroupEnum.parse(userGroup);
        ValidUtils.requireNonNull(userGroupEnum, "Incorrect user group");
        ValidUtils.requireNotEmpty(multipartFiles, "No file uploaded");
        ValidUtils.requireNotEmpty(fileNames, "No file uploaded");

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
        return Result.ok();
    }

    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody download(@PathParam("uuid") String uuid, HttpServletResponse resp, HttpServletRequest req) throws MsgEmbeddedException,
            InvalidAuthenticationException, IOException {
        final int userId = AuthUtil.getUserId();
        // validate user authority
        fileInfoService.validateUserDownload(userId, uuid);
        // get fileName
        final String filename = fileInfoService.getFilename(uuid);
        // set header for the downloaded file
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodeAttachmentName(filename));

        // negotiate whether we should use gzip or plain streaming
        Enumeration<String> encodings = req.getHeaders(HttpHeaders.ACCEPT_ENCODING);
        boolean useGzip = false;
        while (encodings.hasMoreElements()) {
            if (encodings.nextElement().trim().equalsIgnoreCase("gzip"))
                useGzip = true;
        }

        // write file directly to outputStream without holding servlet's thread
        InputStream in = fileInfoService.retrieveFileInputStream(uuid);
        if (useGzip) {
            resp.addHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
            return new GzipStreamingResponseBody(in);
        } else
            return new PlainStreamingResponseBody(in);
    }

    @PostMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileInfoRespVo> listAll(@RequestBody ListFileInfoReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {
        ValidUtils.requireNonNull(reqVo.getPagingVo());
        ValidUtils.requireNonNull(reqVo.getPagingVo().getLimit());
        ValidUtils.requireNonNull(reqVo.getPagingVo().getPage());
        reqVo.setUserId(AuthUtil.getUserId());
        PageInfo<FileInfoVo> fileInfoVoPageInfo = fileInfoService.findPagedFilesForUser(reqVo);
        PagingVo paging = new PagingVo();
        paging.setTotal(fileInfoVoPageInfo.getTotal());
        return Result.of(new ListFileInfoRespVo(fileInfoVoPageInfo.getList(), paging));
    }

    @PostMapping(path = "/delete")
    public Result<Void> deleteFile(@RequestBody LogicDeleteFileReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {
        ValidUtils.requireNonNull(reqVo.getUuid());
        fileInfoService.deleteFileLogically(AuthUtil.getUserId(), reqVo.getUuid());
        return Result.ok();
    }

    @GetMapping(path = "/extension/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<String>> listSupportedFileExtensionNames() {
        return Result.of(
                fileExtensionService.getNamesOfAllEnabled()
        );
    }

    @PostMapping(path = "/extension/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileExtRespVo> listSupportedFileExtensionDetails(@RequestBody ListFileExtReqVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getPagingVo());
        PageInfo<FileExtVo> pageInfo = fileExtensionService.getDetailsOfAllByPageSelective(vo);
        PagingVo pagingVo = new PagingVo();
        pagingVo.setTotal(pageInfo.getTotal());
        return Result.of(new ListFileExtRespVo(pageInfo.getList(), pagingVo));
    }

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
