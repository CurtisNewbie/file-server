package com.yongj.web;

import com.curtisnewbie.common.advice.RoleControlled;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.PageableVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.service.auth.messaging.helper.LogOperation;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdReq;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdResp;
import com.yongj.converters.FileInfoConverter;
import com.yongj.converters.FileSharingConverter;
import com.yongj.converters.TagConverter;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FExtIsEnabled;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.io.PathResolver;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileService;
import com.yongj.services.TempTokenFileDownloadService;
import com.yongj.util.PathUtils;
import com.yongj.vo.*;
import com.yongj.web.streaming.ChannelStreamingResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.trace.TraceUtils.tUser;
import static com.curtisnewbie.common.util.AssertUtils.*;
import static com.curtisnewbie.common.util.BeanCopyUtils.mapTo;
import static com.curtisnewbie.common.util.PagingUtil.forPage;

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

    /**
     * Upload file, only user and admin are allowed to upload file (guest is not allowed)
     */
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<?> upload(@RequestParam("fileName") String fileName,
                            @RequestParam("file") MultipartFile[] multipartFiles,
                            @RequestParam("userGroup") int userGroup) throws IOException {

        final FileUserGroupEnum userGroupEnum = FileUserGroupEnum.parse(userGroup);
        AssertUtils.nonNull(userGroupEnum, "Incorrect user group");
        AssertUtils.notEmpty(multipartFiles, "No file uploaded");
        hasText(fileName, "File name can'tb be empty");

        // only validate the first fileName, if there is only one file, this will be the name of the file
        // if there are multiple files, this will be the name of the zip file
        pathResolver.validateFileExtension(fileName);
        TUser tUser = tUser();

        if (multipartFiles.length == 1) {
            fileInfoService.uploadFile(UploadFileVo.builder()
                    .userId(tUser.getUserId())
                    .username(tUser.getUsername())
                    .fileName(fileName)
                    .userGroup(userGroupEnum)
                    .inputStream(multipartFiles[0].getInputStream())
                    .build());
        } else {
            /*
                upload multiple files, compress them into a single file zip file
                the first one is the zipFile's name, and the rest are the entries
             */
            fileInfoService.uploadFilesAsZip(UploadZipFileVo.builder()
                    .userId(tUser.getUserId())
                    .username(tUser.getUsername())
                    .zipFile(fileName)
                    .userGroup(userGroupEnum)
                    .multipartFiles(multipartFiles)
                    .build());
        }
        return Result.ok();
    }

    /**
     * Grant access to the file to another user (only file uploader can do so)
     */
    @LogOperation(name = "grantAccessToUser", description = "Grant file access")
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping(path = "/grant-access")
    public Result<Void> grantAccessToUser(@RequestBody GrantAccessToUserReqVo v) {
        final TUser tUser = tUser();

        v.validate();
        final String grantedToUsername = v.getGrantedTo();
        final Result<Integer> result = userServiceFeign.findIdByUsername(grantedToUsername);
        result.assertIsOk();

        Integer grantedToId = result.getData();
        AssertUtils.nonNull(grantedToId, "User '" + grantedToUsername + "' doesn't exist");

        fileInfoService.grantFileAccess(GrantFileAccessCmd.builder()
                .fileId(v.getFileId())
                .grantedByName(tUser.getUsername())
                .grantedByUserId(tUser.getUserId())
                .grantedTo(grantedToId)
                .build());
        return Result.ok();
    }

    /**
     * List accesses granted to the file (for current user)
     */
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping(path = "/list-granted-access")
    public Result<ListGrantedAccessRespVo> listGrantedAccess(@Validated @RequestBody ListGrantedAccessReqVo v) {

        final PageablePayloadSingleton<List<FileSharingVo>> pps = fileInfoService.listGrantedAccess(v.getFileId(),
                tUser().getUserId(), v.getPagingVo());

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

    /**
     * Remove the access granted to a user (for current user, and only file uploader can do it)
     */
    @LogOperation(name = "removeGrantedFileAccess", description = "Remove granted file access")
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping(path = "/remove-granted-access")
    public Result<Void> removeGrantedFileAccess(@Validated @RequestBody RemoveGrantedFileAccessReqVo v) {
        fileInfoService.removeGrantedAccess(v.getFileId(), v.getUserId(), tUser().getUserId());
        return Result.ok();
    }

    /**
     * List accessible files for current user
     */
    @PostMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<ListFileInfoRespVo> listFiles(@RequestBody ListFileInfoReqVo reqVo) {
        reqVo.setUserId(tUser().getUserId());
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

    /**
     * Delete file (only file uploader can do it)
     */
    @LogOperation(name = "deleteFile", description = "Delete a file logically")
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping(path = "/delete")
    public Result<Void> deleteFile(@RequestBody @Valid LogicDeleteFileReqVo reqVo) throws InvalidAuthenticationException {
        AssertUtils.nonNull(reqVo.getId());
        fileInfoService.deleteFileLogically(tUser().getUserId(), reqVo.getId());
        return Result.ok();
    }

    /**
     * List supported file extension names
     */
    @GetMapping(path = "/extension/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<List<String>> listSupportedFileExtensionNames() {
        return Result.of(
                fileExtensionService.getNamesOfAllEnabled()
        );
    }

    /**
     * Add a new file extension
     */
    @LogOperation(name = "addFileExtension", description = "Add file extension")
    @RoleControlled(rolesRequired = "admin")
    @PostMapping("/extension/add")
    public Result<Void> addFileExtension(@RequestBody AddFileExtReqVo reqVo) {
        hasText(reqVo.getName(), "extension name must not be empty");
        FileExtension ext = new FileExtension();
        // by default disabled
        ext.setIsEnabled(FExtIsEnabled.DISABLED);
        ext.setName(reqVo.getName());
        ext.setCreateBy(tUser().getUsername());
        ext.setCreateTime(LocalDateTime.now());
        fileExtensionService.addFileExt(ext);
        return Result.ok();
    }

    /**
     * List details of all file extensions
     */
    @RoleControlled(rolesRequired = "admin")
    @PostMapping(path = "/extension/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<PageableVo<List<FileExtVo>>> listFileExtensionDetails(@RequestBody ListFileExtReqVo vo) {
        PageablePayloadSingleton<List<FileExtVo>> pps = fileExtensionService.getDetailsOfAllByPageSelective(vo);
        final PageableVo<List<FileExtVo>> p = new PageableVo<>();
        p.setData(pps.getPayload());
        p.setPagingVo(pps.getPagingVo());
        return Result.of(p);
    }

    /**
     * Update file extension
     */
    @RoleControlled(rolesRequired = "admin")
    @LogOperation(name = "updateFileExtension", description = "Update file extension")
    @PostMapping(path = "/extension/update")
    public Result<Void> updateFileExtension(@RequestBody UpdateFileExtReq req) {
        nonNull(req.getId());
        notNull(req.getIsEnabled());

        fileExtensionService.updateFileExtension(req);
        return Result.ok();
    }

    /**
     * Update file's info (only uploader can do it)
     */
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping("/info/update")
    public Result<Void> updateFileInfo(@RequestBody UpdateFileReqVo reqVo) throws MsgEmbeddedException,
            InvalidAuthenticationException {

        // validate param
        reqVo.validate();
        TUser tUser = tUser();

        fileInfoService.updateFile(UpdateFileCmd.builder()
                .id(reqVo.getId())
                .fileName(reqVo.getName())
                .userGroup(EnumUtils.parse(reqVo.getUserGroup(), FileUserGroupEnum.class))
                .updatedById(tUser.getUserId())
                .updatedByName(tUser.getUsername())
                .build());
        return Result.ok();
    }

    /**
     * Generate temporary token to download the file
     */
    @PostMapping("/token/generate")
    public Result<String> generateTempToken(@Valid @RequestBody GenerateTokenReqVo reqVo) {

        final TUser tUser = tUser();

        // validate user authority
        final Integer fileId = reqVo.getId();
        fileInfoService.validateUserDownload(tUser.getUserId(), fileId);

        return Result.of(tempTokenFileDownloadService.generateTempTokenForFile(fileId, 15));
    }

    /**
     * Download file by a generated token
     */
    @GetMapping("/token/download")
    public StreamingResponseBody downloadByToken(HttpServletRequest req, HttpServletResponse resp,
                                                 @PathParam("token") String token) throws IOException,
            MsgEmbeddedException {

        hasText(token, "Token can't be empty");
        final Integer id = tempTokenFileDownloadService.getIdByToken(token);
        notNull(id, "Token is invalid or expired");

        FileInfo fi = fileInfoService.findById(id);
        notNull(fi, "File not found");
        if (!Objects.equals(fi.getIsLogicDeleted(), FileLogicDeletedEnum.NORMAL.getValue())) {
            // remove the token
            tempTokenFileDownloadService.removeToken(token);
            throw new MsgEmbeddedException("File is deleted already");
        }

        return download(req, resp, fi);
    }

    /**
     * List all tags for current user
     */
    @GetMapping("/tag/list/all")
    public Result<List<String>> listAllTags() throws InvalidAuthenticationException {
        return Result.of(fileInfoService.listFileTags(tUser().getUserId()));
    }

    /**
     * List all tags for the current user and the selected file
     */
    @PostMapping("/tag/list-for-file")
    public Result<PageableVo<List<TagWebVo>>> listTagsForFile(@Validated @RequestBody ListTagsForFileWebReqVo req) {
        PageableVo<List<TagVo>> pv = fileInfoService.listFileTags(tUser().getUserId(), req.getFileId(), forPage(req.getPagingVo()));
        return Result.of(PagingUtil.convert(pv, tagConverter::toWebVo));
    }

    /**
     * Tag the file (only for current user)
     */
    @PostMapping("/tag")
    public Result<Void> tagFile(@Validated @RequestBody TagFileWebReqVo req) {
        final TUser tUser = tUser();
        fileInfoService.tagFile(TagFileCmd.builder()
                .fileId(req.getFileId())
                .tagName(req.getTagName())
                .userId(tUser.getUserId())
                .taggedBy(tUser.getUsername())
                .build());

        return Result.ok();
    }

    /**
     * Remove the tag for the file (only for current user)
     */
    @PostMapping("/untag")
    public Result<Void> untagFile(@Validated @RequestBody UntagFileWebReqVo req) {
        final TUser tUser = tUser();
        fileInfoService.untagFile(UntagFileCmd.builder()
                .fileId(req.getFileId())
                .tagName(req.getTagName())
                .userId(tUser.getUserId())
                .untaggedBy(tUser.getUsername())
                .build());

        return Result.ok();
    }

    // ----------------------------------------- private helper methods -----------------------------------

    private StreamingResponseBody download(HttpServletRequest req, HttpServletResponse resp, FileInfo fi) throws IOException {
        // set header for the downloaded file
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + encodeAttachmentName(fi.getName()));
        resp.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fi.getSizeInBytes()));

        // use FileChannel#transferTo to download file
        return new ChannelStreamingResponseBody(fileInfoService.retrieveFileChannel(fi.getId()), fi.getName());
    }

    private static String encodeAttachmentName(String filePath) throws UnsupportedEncodingException {
        return URLEncoder.encode(PathUtils.extractFileName(filePath), StandardCharsets.UTF_8.name());
    }

    /** negotiate whether we should use gzip or plain streaming */
    private static boolean useGzip(HttpServletRequest req) {
        Enumeration<String> encodings = req.getHeaders(HttpHeaders.ACCEPT_ENCODING);
        boolean useGzip = false;
        while (encodings.hasMoreElements()) {
            if (encodings.nextElement().trim().equalsIgnoreCase("gzip"))
                useGzip = true;
        }
        return useGzip;
    }

}
