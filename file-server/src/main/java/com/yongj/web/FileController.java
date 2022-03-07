package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PageableVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.aop.LogOperation;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdReq;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdResp;
import com.curtisnewbie.service.auth.remote.vo.UserVo;
import com.yongj.converters.FileInfoConverter;
import com.yongj.converters.FileSharingConverter;
import com.yongj.converters.TagConverter;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FileExtensionIsEnabledEnum;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.io.PathResolver;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileService;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.util.AssertUtils.nonNull;
import static com.curtisnewbie.common.util.BeanCopyUtils.mapTo;
import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.module.auth.util.AuthUtil.getUserId;
import static com.curtisnewbie.module.auth.util.AuthUtil.getUsername;
import static org.springframework.util.StringUtils.hasText;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/file")
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private UserServiceFeign userServiceFeign;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private FileExtensionService fileExtensionService;
    @Autowired
    private FileService fileInfoService;
    @Autowired
    private TempTokenFileDownloadService tempTokenFileDownloadService;
    @Autowired
    private FileInfoConverter fileInfoConverter;
    @Autowired
    private FileSharingConverter fileSharingConverter;
    @Autowired
    private TagConverter tagConverter;

    @LogOperation(name = "/file/upload", description = "upload file")
    @PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> upload(@RequestParam("fileName") String[] fileNames,
                            @RequestParam("file") MultipartFile[] multipartFiles,
                            @RequestParam("userGroup") int userGroup) throws IOException {

        final FileUserGroupEnum userGroupEnum = FileUserGroupEnum.parse(userGroup);
        AssertUtils.nonNull(userGroupEnum, "Incorrect user group");
        AssertUtils.notEmpty(multipartFiles, "No file uploaded");
        AssertUtils.notEmpty(fileNames, "No file uploaded");

        // only validate the first fileName, if there is only one file, this will be the name of the file
        // if there are multiple files, this will be the name of the zip file
        pathResolver.validateFileExtension(fileNames[0]);

        if (multipartFiles.length == 1) {
            fileInfoService.uploadFile(UploadFileVo.builder()
                    .userId(getUserId())
                    .username(AuthUtil.getUsername())
                    .fileName(fileNames[0])
                    .userGroup(userGroupEnum)
                    .inputStream(multipartFiles[0].getInputStream())
                    .build());
        } else {
            /*
                upload multiple files, compress them into a single file zip file
                the first one is the zipFile's name, and the rest are the entries
             */
            AssertUtils.equals(fileNames.length, multipartFiles.length + 1);
            String zipFile = fileNames[0];
            String[] entryNames = Arrays.copyOfRange(fileNames, 1, fileNames.length);
            fileInfoService.uploadFilesAsZip(UploadZipFileVo.builder()
                    .userId(getUserId())
                    .username(AuthUtil.getUsername())
                    .zipFile(zipFile)
                    .entryNames(entryNames)
                    .userGroup(userGroupEnum)
                    .inputStreams(collectInputStreams(multipartFiles))
                    .build());
        }
        return Result.ok();
    }

    @LogOperation(name = "/file/grant-access", description = "grant file's access to other user")
    @PostMapping(path = "/grant-access")
    public Result<Void> grantAccessToUser(@RequestBody GrantAccessToUserReqVo v) {
        v.validate();
        final String grantedToUsername = v.getGrantedTo();
        final Result<Integer> result = userServiceFeign.findIdByUsername(grantedToUsername);
        result.assertIsOk();

        Integer grantedToId = result.getData();
        AssertUtils.nonNull(grantedToId, "User '" + grantedToUsername + "' doesn't exist");

        fileInfoService.grantFileAccess(GrantFileAccessCmd.builder()
                .fileId(v.getFileId())
                .grantedBy(AuthUtil.getUsername())
                .grantedTo(grantedToId)
                .build());
        return Result.ok();
    }

    @PostMapping(path = "/list-granted-access")
    public Result<ListGrantedAccessRespVo> listGrantedAccess(@Validated @RequestBody ListGrantedAccessReqVo v) {
        final PageablePayloadSingleton<List<FileSharingVo>> pps = fileInfoService.listGrantedAccess(v.getFileId(), v.getPagingVo());
        final ListGrantedAccessRespVo resp = new ListGrantedAccessRespVo();
        // collect list of userIds
        List<Integer> idList = pps.getPayload().stream().map(FileSharingVo::getUserId).collect(Collectors.toList());
        if (!idList.isEmpty()) {
            // get usernames of these userIds
            final Result<FetchUsernameByIdResp> result = userServiceFeign.fetchUsernameById(
                    FetchUsernameByIdReq.builder()
                            .userIds(idList)
                            .build());
            result.assertIsOk();
            Map<Integer, String> idToName = result.getData().getIdToUsername();
            resp.setList(BeanCopyUtils.mapTo(pps.getPayload(), vo -> {
                // convert to FileSharingWebVo
                FileSharingWebVo wv = fileSharingConverter.toWebVo(vo);
                wv.setUsername(idToName.get(wv.getUserId()));
                return wv;
            }));
        }
        resp.setPagingVo(pps.getPagingVo());
        return Result.of(resp);
    }

    @LogOperation(name = "/file/remove-granted-access", description = "remove granted file's access")
    @PostMapping(path = "/remove-granted-access")
    public Result<Void> removeGrantedFileAccess(@Validated @RequestBody RemoveGrantedFileAccessReqVo v) {

        fileInfoService.removeGrantedAccess(v.getFileId(), v.getUserId(), getUserId());
        return Result.ok();
    }

    @LogOperation(name = "/file/download", description = "download file")
    @GetMapping(path = "/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public StreamingResponseBody download(@PathParam("id") int id, HttpServletResponse resp, HttpServletRequest req)
            throws InvalidAuthenticationException, IOException {

        final int userId = getUserId();

        // validate user authority
        fileInfoService.validateUserDownload(userId, id);

        // get fileInfo
        final FileInfo fi = fileInfoService.findById(id);
        nonNull(fi, "File not found");

        return download(req, resp, fi);
    }

    @PostMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileInfoRespVo> listAll(@RequestBody ListFileInfoReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {
        // validate param
        reqVo.validate();

        reqVo.setUserId(getUserId());
        PageablePayloadSingleton<List<FileInfoVo>> pageable = fileInfoService.findPagedFilesForUser(reqVo);

        // collect list of ids to request their usernames, if uploader name is absent
        final List<Integer> uploaderIds = pageable.getPayload().stream()
                .filter(f -> f.getUploaderName() == null)
                .map(FileInfoVo::getUploaderId)
                .collect(Collectors.toList());

        if (!uploaderIds.isEmpty()) {
            final Result<FetchUsernameByIdResp> result = userServiceFeign.fetchUsernameById(FetchUsernameByIdReq.builder()
                    .userIds(uploaderIds)
                    .build());
            result.assertIsOk();
            Map<Integer, String> idToName = result.getData().getIdToUsername();
            pageable.getPayload().forEach(f -> {
                int id = f.getUploaderId();
                if (idToName.containsKey(id))
                    f.setUploaderName(idToName.get(id));
            });
        }

        final ListFileInfoRespVo res = new ListFileInfoRespVo();
        res.setFileInfoList(mapTo(pageable.getPayload(), fileInfoConverter::toWebVo));
        res.setPagingVo(pageable.getPagingVo());
        return Result.of(res);
    }

    @LogOperation(name = "/file/delete", description = "delete file")
    @PostMapping(path = "/delete")
    public Result<Void> deleteFile(@RequestBody @Valid LogicDeleteFileReqVo reqVo) throws InvalidAuthenticationException {
        AssertUtils.nonNull(reqVo.getId());
        fileInfoService.deleteFileLogically(getUserId(), reqVo.getId());
        return Result.ok();
    }

    @GetMapping(path = "/extension/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<String>> listSupportedFileExtensionNames() {
        return Result.of(
                fileExtensionService.getNamesOfAllEnabled()
        );
    }

    @LogOperation(name = "/file/extension/add", description = "add file extension")
    @PostMapping("/extension/add")
    public Result<Void> addFileExtension(@RequestBody AddFileExtReqVo reqVo) {
        AssertUtils.hasText(reqVo.getName(), "extension name must not be empty");
        FileExtension ext = new FileExtension();
        // by default disabled
        ext.setIsEnabled(FileExtensionIsEnabledEnum.DISABLED.getValue());
        ext.setName(reqVo.getName());
        ext.setCreateBy(AuthUtil.getUsername());
        ext.setCreateTime(LocalDateTime.now());
        fileExtensionService.addFileExt(ext);
        return Result.ok();
    }

    @PostMapping(path = "/extension/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileExtRespVo> listSupportedFileExtensionDetails(@RequestBody ListFileExtReqVo vo) throws MsgEmbeddedException {
        vo.validate();

        PageablePayloadSingleton<List<FileExtVo>> dataList = fileExtensionService.getDetailsOfAllByPageSelective(vo);
        ListFileExtRespVo res = new ListFileExtRespVo(dataList.getPayload());
        res.setPagingVo(dataList.getPagingVo());
        return Result.of(res);
    }

    @LogOperation(name = "/file/extension/update", description = "update status of supported file extension")
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping(path = "/extension/update")
    public Result<Void> updateFileExtensionStatus(@RequestBody FileExtVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getId());
        // either the name or isEnabled should be entered
        if (vo.getIsEnabled() == null && !hasText(vo.getName())) {
            throw new MsgEmbeddedException("Required parameters should not be null");
        }
        // check if the isEnabled value is valid
        FileExtensionIsEnabledEnum isEnabledEnum = EnumUtils.parse(vo.getIsEnabled(),
                FileExtensionIsEnabledEnum.class);
        AssertUtils.nonNull(isEnabledEnum, "isEnabled value illegal");
        fileExtensionService.updateFileExtSelective(vo);
        return Result.ok();
    }

    @LogOperation(name = "/file/info/update", description = "update file info")
    @PreAuthorize("hasAuthority('admin') || hasAuthority('user')")
    @PostMapping("/info/update")
    public Result<Void> updateFileInfo(@RequestBody UpdateFileReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {

        // validate param
        reqVo.validate();

        fileInfoService.updateFile(UpdateFileCmd.builder()
                .id(reqVo.getId())
                .fileName(reqVo.getName())
                .userGroup(EnumUtils.parse(reqVo.getUserGroup(), FileUserGroupEnum.class))
                .updatedById(getUserId())
                .updatedByName(getUsername())
                .build());
        return Result.ok();
    }

    @LogOperation(name = "/file/token/generate", description = "generate temp token for file download")
    @PreAuthorize("hasAuthority('user') || hasAuthority('admin')")
    @PostMapping("/token/generate")
    public Result<String> generateToken(@RequestBody GenerateTokenReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException, NoSuchMethodException {
        ValidUtils.requireNonNull(reqVo.getId(), "id can't be empty");
        FileInfo fi = fileInfoService.findById(reqVo.getId());
        ValidUtils.requireNonNull(fi, "File not found");

        if (!Objects.equals(fi.getUploaderId(), getUserId())) {
            throw new MsgEmbeddedException("Only the owner of the file can generate temporary token");
        }

        final String token = tempTokenFileDownloadService.generateTempTokenForFile(reqVo.getId());
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
            MsgEmbeddedException {

        Optional<UserVo> optionalUserEntity = AuthUtil.getOptionalUser();
        String username = optionalUserEntity.isPresent() ? optionalUserEntity.get().getUsername() : "Anonymous";
        logger.info("User {} attempts to download file using token {}", username, token);

        ValidUtils.requireNotEmpty(token, "Token can't be empty");
        final Integer id = tempTokenFileDownloadService.getIdByToken(token);
        ValidUtils.requireNonNull(id, "Token is invalid or expired");

        FileInfo fi = fileInfoService.findById(id);
        ValidUtils.requireNonNull(fi, "File not found");
        if (!Objects.equals(fi.getIsLogicDeleted(), FileLogicDeletedEnum.NORMAL.getValue())) {
            // remove the token
            tempTokenFileDownloadService.removeToken(token);
            throw new MsgEmbeddedException("File is deleted already");
        }

        return download(req, resp, fi);
    }

    @GetMapping("/tag/list/all")
    public Result<List<String>> listAllTags() throws InvalidAuthenticationException {
        return Result.of(fileInfoService.listFileTags(getUserId()));
    }

    @PostMapping("/tag/list-for-file")
    public Result<PageableVo<List<TagWebVo>>> listTagsForFile(@Validated @RequestBody ListTagsForFileWebReqVo req) throws InvalidAuthenticationException {
        PageableVo<List<TagVo>> pv = fileInfoService.listFileTags(getUserId(), req.getFileId(), forPage(req.getPagingVo()));
        return Result.of(PagingUtil.convert(pv, tagConverter::toWebVo));
    }

    @PostMapping("/tag")
    public Result<Void> tagFile(@Validated @RequestBody TagFileWebReqVo req) throws InvalidAuthenticationException {
        fileInfoService.tagFile(TagFileCmd.builder()
                .fileId(req.getFileId())
                .tagName(req.getTagName())
                .userId(getUserId())
                .taggedBy(AuthUtil.getUsername())
                .build());

        return Result.ok();
    }

    @PostMapping("/untag")
    public Result<Void> untagFile(@Validated @RequestBody UntagFileWebReqVo req) throws InvalidAuthenticationException {
        fileInfoService.untagFile(UntagFileCmd.builder()
                .fileId(req.getFileId())
                .tagName(req.getTagName())
                .userId(getUserId())
                .untaggedBy(AuthUtil.getUsername())
                .build());

        return Result.ok();
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
        InputStream in = fileInfoService.retrieveFileInputStream(fi.getId());
        if (useGzip) {
            resp.addHeader(HttpHeaders.CONTENT_ENCODING, "gzip");
            return new GzipStreamingResponseBody(in);
        } else
            return new PlainStreamingResponseBody(in);
    }

    private static String encodeAttachmentName(String filePath) throws UnsupportedEncodingException {
        return URLEncoder.encode(PathUtils.extractFileName(filePath), StandardCharsets.UTF_8.name());
    }

    private static InputStream[] collectInputStreams(MultipartFile[] files) throws IOException {
        InputStream[] inputStreams = new InputStream[files.length];
        for (int i = 0; i < files.length; i++)
            inputStreams[i] = files[i].getInputStream();
        return inputStreams;
    }
}
