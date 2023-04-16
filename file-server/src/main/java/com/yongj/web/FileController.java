package com.yongj.web;

import brave.Span;
import brave.Tracer;
import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.AsyncUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.Runner;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.goauth.client.PathDoc;
import com.curtisnewbie.goauth.client.PathType;
import com.curtisnewbie.module.redisutil.RedisController;
import com.curtisnewbie.service.auth.messaging.helper.LogOperation;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdReq;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByIdResp;
import com.yongj.converters.FileSharingConverter;
import com.yongj.converters.TagConverter;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileInfo;
import com.yongj.enums.FExtIsEnabled;
import com.yongj.enums.FLogicDelete;
import com.yongj.enums.FUserGroup;
import com.yongj.enums.TokenType;
import com.yongj.io.PathResolver;
import com.yongj.io.operation.MediaStreamingUtils;
import com.yongj.services.FileExtensionService;
import com.yongj.services.FileService;
import com.yongj.services.TempTokenFileDownloadService;
import com.yongj.services.qry.VFolderQueryService;
import com.yongj.util.PathUtils;
import com.yongj.vo.*;
import com.yongj.web.streaming.ChannelStreamingResponseBody;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.trace.TraceUtils.tUser;
import static com.curtisnewbie.common.util.AssertUtils.*;
import static com.curtisnewbie.common.util.AsyncUtils.runAsync;
import static com.curtisnewbie.common.util.AsyncUtils.runAsyncResult;
import static com.curtisnewbie.common.util.PagingUtil.convertPayload;
import static com.curtisnewbie.common.util.PagingUtil.forPage;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Validated
@RestController
@RequestMapping("${web.base-path}/file")
@PathDoc(resourceCode = Resources.MANAGE_FILE_CODE, resourceName = Resources.MANAGE_FILE_NAME)
public class FileController {

    public static final long SIZE_MB_10 = 10 * 1024 * 1024;

    @Autowired
    private VFolderQueryService vFolderQueryService;
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
    private FileSharingConverter fileSharingConverter;
    @Autowired
    private TagConverter tagConverter;
    @Autowired
    private Tracer tracer;
    @Autowired
    private RedisController redisController;

    /**
     * Preflight check on whether the filename exists already
     */
    @PathDoc(description = "User - preflight check for duplicate file uploads")
    @GetMapping("/upload/duplication/preflight")
    public Result<Boolean> handleDuplicateOnNamePreflightCheck(@RequestParam("fileName") String fileName,
                                                               @RequestParam(value = "parentFileKey", required = false) String parentFileKey) {
        parentFileKey = parentFileKey.trim();
        if (!StringUtils.hasText(parentFileKey)) parentFileKey = "";
        return Result.of(fileInfoService.filenameExists(fileName, parentFileKey, tUser().getUserId()));
    }

    /**
     * Fetch parent file info
     */
    @PathDoc(description = "User fetch parent file info")
    @GetMapping("/parent")
    public DeferredResult<Result<ParentFileInfo>> fetchParentFileInfo(@RequestParam("fileKey") String fileKey) {
        return AsyncUtils.runAsyncResult(() -> fileInfoService.getParentFileInfo(fileKey, tUser()));
    }

    /**
     * Upload file using stream
     * <p>
     * Only supports a single file upload, but since we are using stream, it can handle pretty large file in an
     * efficient way
     */
    @PathDoc(description = "User upload file via streaming")
    @PostMapping(path = "/upload/stream", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Result<Void> streamUpload(@RequestHeader("fileName") String fileName,
                                     @RequestHeader(value = "userGroup") Integer userGroupInt,
                                     @RequestHeader(value = "tag", required = false) @Nullable String[] tags,
                                     @RequestHeader(value = "parentFile", required = false) @Nullable String parentFile,
                                     HttpServletRequest request) throws IOException {

        final FUserGroup userGroup = FUserGroup.from(userGroupInt);
        nonNull(userGroup, "Incorrect user group");
        hasText(fileName, "File name can't be empty");
        fileName = URLDecoder.decode(fileName, "UTF-8").trim();

        final TUser tUser = tUser();

        // We are streaming the data, it must be synchronous
        final FileInfo f = fileInfoService.uploadFile(UploadFileVo.builder()
                .userNo(tUser.getUserNo())
                .userId(tUser.getUserId())
                .username(tUser.getUsername())
                .fileName(fileName)
                .userGroup(userGroup)
                .inputStream(request.getInputStream())
                .parentFile(parentFile)
                .build());
        log.info("File uploaded and persisted in database, file_info: {}", f);

        // attempt to propagate trace
        final Span span = tracer.nextSpan();
        CompletableFuture.runAsync(() -> {
            try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                // add tags
                if (tags != null && tags.length > 0) {
                    // todo repeated code :D
                    log.info("Adding tags to new file {} ({}), tags: {}", f.getName(), f.getUuid(), tags);
                    for (String tag : tags) {
                        if (!StringUtils.hasText(tag)) continue;
                        fileInfoService.tagFile(TagFileCmd.builder()
                                .fileId(f.getId())
                                .tagName(tag)
                                .userId(tUser.getUserId())
                                .build());
                    }
                }
            }
        }).whenComplete((v, e) -> {
            if (e != null)
                log.error("Exception occurred while add tags to files, uuid: {}", f.getUuid(), e);
        });

        return Result.ok();
    }

    /**
     * Upload file, only user and admin are allowed to upload file (guest is not allowed)
     */
    @PathDoc(description = "User upload files")
    @PostMapping(path = "/upload", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> upload(@RequestParam("fileName") String fileName,
                               @RequestParam("file") MultipartFile[] multipartFiles,
                               @RequestParam(value = "parentFile", required = false) String parentFile,
                               @RequestParam(value = "tag", required = false) @Nullable String[] tags,
                               @RequestParam("userGroup") int userGroup) throws IOException {

        final FUserGroup userGroupEnum = FUserGroup.parse(userGroup);
        AssertUtils.nonNull(userGroupEnum, "Incorrect user group");
        AssertUtils.notEmpty(multipartFiles, "No file uploaded");
        hasText(fileName, "File name can't be empty");
        var fname = fileName.trim();

        // only validate the first fileName, if there is only one file, this will be the name of the file
        // if there are multiple files, this will be the name of the zip file
        TUser tUser = tUser();

        /*
        It turns out that the servlet engine like tomcat actually caches the uploaded file to disk (or memory
        if it's small). When it calls this method, it essentially means that the multipart file has been fully
        cached, and what we are doing here is very likely just moving file from cache to our folders. The uploading
        may fail while we are 'copying', it doesn't matter, cas we don't want the clients to stay idle while they
        have done their part.
         */
        CompletableFuture<FileInfo> future = CompletableFuture.supplyAsync(() ->
                Runner.tryCall(() -> {
                    if (multipartFiles.length == 1) {
                        return fileInfoService.uploadFile(UploadFileVo.builder()
                                .userId(tUser.getUserId())
                                .username(tUser.getUsername())
                                .fileName(fname)
                                .userGroup(userGroupEnum)
                                .inputStream(multipartFiles[0].getInputStream())
                                .parentFile(parentFile)
                                .build());
                    } else {
                        /*
                            upload multiple files, compress them into a single file zip file
                            the first one is the zipFile's name, and the rest are the entries
                        */
                        return fileInfoService.uploadFilesAsZip(UploadZipFileVo.builder()
                                .userNo(tUser.getUserNo())
                                .userId(tUser.getUserId())
                                .username(tUser.getUsername())
                                .zipFile(fname)
                                .userGroup(userGroupEnum)
                                .multipartFiles(multipartFiles)
                                .parentFile(parentFile)
                                .build());
                    }
                }));
        log.info("File uploaded, processing asynchronously, file_name: {}", fileName);

        // attempt to propagate tracing
        final Span span = tracer.currentSpan();

        // todo repeated code :D
        future.whenCompleteAsync((f, e) -> {
            log.info("File uploaded and persisted in database, file_name: {}, file_info: {}", fileName, f, e);

            if (tags != null && tags.length > 0) {
                log.info("Adding tags to new file {} ({}), tags: {}", f.getName(), f.getUuid(), tags);

                try (Tracer.SpanInScope ws = tracer.withSpanInScope(span)) {
                    for (String tag : tags) {
                        if (!StringUtils.hasText(tag)) continue;
                        fileInfoService.tagFile(TagFileCmd.builder()
                                .fileId(f.getId())
                                .tagName(tag)
                                .userId(tUser.getUserId())
                                .build());
                    }
                }
            }
        });
        return Result.ok();
    }

    /**
     * Export selected files as zip
     */
    @PathDoc(description = "User export files")
    @LogOperation(name = "exportAsZip", description = "Export As Zip")
    @PostMapping(path = "/export-as-zip")
    public Result<Void> exportAsZip(@RequestBody ExportAsZipReq r) {
        final TUser user = tUser();
        final List<Integer> fileIds = r.getFileIds()
                .stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        AssertUtils.isTrue(!fileIds.isEmpty(), "Please select files first");

        // merely an optimization, may not accurate
        if (redisController.exists("fs:export:zip:" + user.getUserNo())) {
            return Result.error("Your are currently exporting a zip file, please wait before it finishes");
        }

        CompletableFuture.runAsync(() -> fileInfoService.exportAsZip(r, user), AsyncUtils.getCommonWorkStealingPool());

        return Result.ok();
    }

    /**
     * Move file into directory
     */
    @PathDoc(description = "User move files into directory")
    @LogOperation(name = "moveIntoDir", description = "Move into directory")
    @PostMapping(path = "/move-to-dir")
    public DeferredResult<Result<Void>> moveFileIntoDir(@RequestBody MoveFileIntoDirReqVo r) {
        return runAsync(() -> {
            final TUser user = tUser();
            fileInfoService.moveFileInto(user.getUserId(), r.getUuid(), r.getParentFileUuid());
        });
    }

    /**
     * Make Directory for current user
     */
    @PathDoc(description = "User make directory")
    @LogOperation(name = "makeDir", description = "Make directory")
    @PostMapping(path = "/make-dir")
    public DeferredResult<Result<String>> makeDir(@RequestBody MakeDirReqVo req) {
        return runAsyncResult(() -> {
            final TUser user = tUser();
            MakeDirCmd cmd = new MakeDirCmd();
            cmd.setUserNo(user.getUserNo());
            cmd.setUploaderId(user.getUserId());
            cmd.setUploaderName(user.getUsername());
            cmd.setName(req.getName());
            cmd.setUserGroup(req.getUserGroup());
            cmd.setParentFile(req.getParentFile());
            final FileInfo dir = fileInfoService.mkdir(cmd);
            return dir.getUuid();
        });
    }

    /**
     * Grant access to the file to another user (only file uploader can do so)
     */
    @PathDoc(description = "User grant file access")
    @LogOperation(name = "grantAccessToUser", description = "Grant file access")
    @PostMapping(path = "/grant-access")
    public DeferredResult<Result<Void>> grantAccessToUser(@RequestBody @Valid GrantAccessToUserReqVo v) {
        return runAsync(() -> {
            final TUser tUser = tUser();
            final String grantedToUsername = v.getGrantedTo();
            final Result<Integer> result = userServiceFeign.findIdByUsername(grantedToUsername);
            result.assertIsOk();

            Integer grantedToId = result.getData();
            AssertUtils.nonNull(grantedToId, "User '" + grantedToUsername + "' doesn't exist");

            fileInfoService.grantFileAccess(GrantFileAccessCmd.builder()
                    .fileId(v.getFileId())
                    .grantedBy(tUser)
                    .grantedTo(grantedToId)
                    .build());
        });
    }

    /**
     * List accesses granted to the file (for current user)
     */
    @PathDoc(description = "User list granted file access")
    @PostMapping(path = "/list-granted-access")
    public DeferredResult<Result<ListGrantedFileAccessRespVo>> listGrantedAccess(@Validated @RequestBody ListGrantedFileAccessReqVo v) {
        return AsyncUtils.runAsyncResult(() -> {
            final PageableList<FileSharingVo> pps = fileInfoService.listGrantedAccess(v.getFileId(), tUser().getUserId(), v.page());
            final ListGrantedFileAccessRespVo resp = new ListGrantedFileAccessRespVo();
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
            return resp;
        });
    }

    /**
     * Remove the access granted to a user (for current user, and only file uploader can do it)
     */
    @PathDoc(description = "User remove granted file access")
    @LogOperation(name = "removeGrantedFileAccess", description = "Remove granted file access")
    @PostMapping(path = "/remove-granted-access")
    public DeferredResult<Result<Void>> removeGrantedFileAccess(@Validated @RequestBody RemoveGrantedFileAccessReqVo v) {
        return runAsync(() -> {
            fileInfoService.removeGrantedAccess(v.getFileId(), v.getUserId(), tUser().getUserId());
        });
    }

    /**
     * List accessible DIRs for current user
     */
    @PathDoc(description = "User list directories")
    @GetMapping(path = "/dir/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Result<List<ListDirVo>>> listDirs() {
        return runAsyncResult(() -> fileInfoService.listDirs(tUser().getUserId()));
    }

    /**
     * List accessible files for current user
     * <p>
     * There two modes: file mode and folder mode
     * <p>
     * When the folderNo is specified, it's the folder mode; depends on which mode is used, different sql queries are
     * also used
     */
    @PathDoc(description = "User list files")
    @PostMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Result<PageableList<FileInfoWebVo>>> listFiles(@RequestBody ListFileInfoReqVo req) {
        final TUser user = tUser();
        if (log.isDebugEnabled()) log.debug("List files, req: {}, user: {}", req, user.getUserNo());

        return runAsyncResult(() -> {
            final PageableList<FileInfoWebVo> pl;
            // folder mode
            if (StringUtils.hasText(req.getFolderNo())) {
                ListVFolderFilesReq fr = new ListVFolderFilesReq();
                fr.setUserId(user.getUserId());
                fr.setUserNo(user.getUserNo());
                fr.setFolderNo(req.getFolderNo());
                fr.setPagingVo(req.getPagingVo());
                pl = vFolderQueryService.listFilesInFolder(fr);
            } else {
                // file mode
                req.setUserId(user.getUserId());
                req.setUserNo(user.getUserNo());
                pl = fileInfoService.findPagedFilesForUser(req);
            }
            return pl;
        });
    }

    /**
     * Delete file (only file uploader can do it)
     */
    @PathDoc(description = "User delete file")
    @LogOperation(name = "deleteFile", description = "Delete a file logically")
    @PostMapping(path = "/delete")
    public DeferredResult<Result<Void>> deleteFile(@RequestBody @Valid LogicDeleteFileReqVo reqVo) {
        return runAsync(() -> {
            fileInfoService.deleteFileLogically(tUser().getUserId(), reqVo.getUuid());
        });
    }

    /**
     * List supported file extension names
     */
    @PathDoc(description = "User list supported file extension names")
    @GetMapping(path = "/extension/name", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Result<List<String>>> listSupportedFileExtensionNames() {
        return runAsyncResult(() -> fileExtensionService.getNamesOfAllEnabled());
    }

    /**
     * Add a new file extension
     */
    @PathDoc(description = "Admin add supported file extension", resourceCode = Resources.ADMIN_FS_CODE, resourceName = Resources.ADMIN_FS_NAME)
    @LogOperation(name = "addFileExtension", description = "Add file extension")
    @PostMapping("/extension/add")
    public DeferredResult<Result<Void>> addFileExtension(@RequestBody AddFileExtReqVo reqVo) {
        hasText(reqVo.getName(), "extension name must not be empty");

        return runAsync(() -> {
            FileExtension ext = new FileExtension();
            ext.setIsEnabled(FExtIsEnabled.DISABLED); // by default disabled
            ext.setName(reqVo.getName());
            fileExtensionService.addFileExt(ext);
        });
    }

    /**
     * List details of all file extensions
     */
    @PathDoc(description = "Admin list supported file extension details", resourceCode = Resources.ADMIN_FS_CODE, resourceName = Resources.ADMIN_FS_NAME)
    @PostMapping(path = "/extension/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public DeferredResult<Result<PageableList<FileExtVo>>> listFileExtensionDetails(@RequestBody ListFileExtReqVo vo) {
        return runAsyncResult(() -> fileExtensionService.getDetailsOfAllByPageSelective(vo));
    }

    /**
     * Update file extension
     */
    @PathDoc(description = "Admin update supported file extension detail", resourceCode = Resources.ADMIN_FS_CODE, resourceName = Resources.ADMIN_FS_NAME)
    @LogOperation(name = "updateFileExtension", description = "Update file extension")
    @PostMapping(path = "/extension/update")
    public DeferredResult<Result<Void>> updateFileExtension(@RequestBody UpdateFileExtReq req) {
        nonNull(req.getId());
        notNull(req.getIsEnabled());

        return runAsync(() -> fileExtensionService.updateFileExtension(req));
    }

    /**
     * Update file's info (only uploader can do it)
     */
    @PathDoc(description = "User update file info")
    @PostMapping("/info/update")
    public DeferredResult<Result<Void>> updateFileInfo(@RequestBody UpdateFileReqVo reqVo) {

        // validate param
        reqVo.validate();

        return runAsync(() -> {
            TUser tUser = tUser();
            fileInfoService.updateFile(UpdateFileCmd.builder()
                    .id(reqVo.getId())
                    .fileName(reqVo.getName())
                    .userGroup(reqVo.getUserGroup())
                    .updatedById(tUser.getUserId())
                    .build());
        });
    }

    /**
     * Extends temp token expiration
     */
    @PathDoc(description = "User refresh temporary token expiration")
    @PostMapping("/token/renew")
    public DeferredResult<Result<Void>> extendsTempTokenExp(@Valid @RequestBody ExtendsTokenExpReqVo req) {
        return runAsync(() -> tempTokenFileDownloadService.extendsStreamingTokenExp(req.getToken(), 15));
    }


    /**
     * Generate temporary token to download/stream (media) the file
     */
    @PathDoc(description = "User generate temporary token")
    @PostMapping("/token/generate")
    public DeferredResult<Result<String>> generateTempToken(@Valid @RequestBody GenerateTokenReqVo reqVo) {

        return runAsyncResult(() -> {
            final TUser tUser = tUser();

            var fileId = fileInfoService.idOfKey(reqVo.getFileKey());
            AssertUtils.notNull(fileId, "File not found");
            fileInfoService.validateUserDownload(tUser.getUserId(), fileId, tUser.getUserNo());

            return tempTokenFileDownloadService.generateTempTokenForFile(fileId, 15,
                    reqVo.getTokenType() != null ? reqVo.getTokenType() : TokenType.DOWNLOAD);
        });
    }

    /**
     * Handle HEAD request for media streaming
     */
    @PathDoc(description = "Test media streaming support", type = PathType.PUBLIC)
    @RequestMapping(value = "/token/media/streaming", method = RequestMethod.HEAD)
    public ResponseEntity<Void> handleStreamingHeadRequest(@RequestParam("token") String token) {

        hasText(token, "Token can't be empty");
        final Integer id = tempTokenFileDownloadService.getIdByToken(token);
        notNull(id, "Token is invalid or expired");

        FileInfo fi = fileInfoService.findById(id);
        notNull(fi, "File not found");

        if (!Objects.equals(fi.getIsLogicDeleted(), FLogicDelete.NORMAL)) {
            // remove the token
            tempTokenFileDownloadService.removeToken(token);
            throw new UnrecoverableException("File is deleted already");
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_LENGTH, fi.getSizeInBytes() + "")
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                .build();
    }

    /**
     * Media streaming by a generated token
     */
    @PathDoc(description = "Stream media", type = PathType.PUBLIC)
    @GetMapping(value = "/token/media/streaming")
    public StreamingResponseBody streamMediaByToken(HttpServletRequest req, HttpServletResponse resp,
                                                    @RequestParam("token") String token) throws IOException {

        hasText(token, "Token can't be empty");
        final Integer id = tempTokenFileDownloadService.getIdByToken(token);
        notNull(id, "Token is invalid or expired");

        FileInfo fi = fileInfoService.findById(id);
        notNull(fi, "File not found");

        if (!Objects.equals(fi.getIsLogicDeleted(), FLogicDelete.NORMAL)) {
            // remove the token
            tempTokenFileDownloadService.removeToken(token);
            throw new UnrecoverableException("File is deleted already");
        }

        return streamMedia(req, resp, fi);
    }


    /**
     * Download file by a generated token
     */
    @PathDoc(description = "Download file", type = PathType.PUBLIC)
    @GetMapping("/token/download")
    public StreamingResponseBody downloadByToken(HttpServletRequest req, HttpServletResponse resp,
                                                 @RequestParam("token") String token) throws IOException {

        hasText(token, "Token can't be empty");
        final Integer id = tempTokenFileDownloadService.getIdByToken(token);
        notNull(id, "Token is invalid or expired");

        FileInfo fi = fileInfoService.findById(id);
        notNull(fi, "File not found");

        if (!Objects.equals(fi.getIsLogicDeleted(), FLogicDelete.NORMAL)) {
            // remove the token
            tempTokenFileDownloadService.removeToken(token);
            throw new UnrecoverableException("File is deleted already");
        }

        return download(req, resp, fi);
    }

    /**
     * List all tags for current user
     */
    @PathDoc(description = "User list all file tags")
    @GetMapping("/tag/list/all")
    public DeferredResult<Result<List<String>>> listAllTags() throws InvalidAuthenticationException {
        return runAsyncResult(() -> fileInfoService.listFileTags(tUser().getUserId()));
    }

    /**
     * List all tags for the current user and the selected file
     */
    @PathDoc(description = "User list tags of file")
    @PostMapping("/tag/list-for-file")
    public DeferredResult<Result<PageableList<TagWebVo>>> listTagsForFile(@Validated @RequestBody ListTagsForFileWebReqVo req) {
        return runAsyncResult(() -> {
            PageableList<TagVo> pv = fileInfoService.listFileTags(tUser().getUserId(), req.getFileId(), forPage(req.getPagingVo()));
            return convertPayload(pv, tagConverter::toWebVo);
        });
    }

    /**
     * Tag the file (only for current user)
     */
    @PathDoc(description = "User tag file")
    @PostMapping("/tag")
    public DeferredResult<Result<Void>> tagFile(@Validated @RequestBody TagFileWebReqVo req) {
        return runAsync(() -> {
            final TUser tUser = tUser();
            fileInfoService.tagFile(TagFileCmd.builder()
                    .fileId(req.getFileId())
                    .tagName(req.getTagName())
                    .userId(tUser.getUserId())
                    .build());
        });
    }

    /**
     * Remove the tag for the file (only for current user)
     */
    @PathDoc(description = "User untag file")
    @PostMapping("/untag")
    public DeferredResult<Result<Void>> untagFile(@Validated @RequestBody UntagFileWebReqVo req) {
        return runAsync(() -> {
            final TUser tUser = tUser();
            fileInfoService.untagFile(UntagFileCmd.builder()
                    .fileId(req.getFileId())
                    .tagName(req.getTagName())
                    .userId(tUser.getUserId())
                    .build());
        });
    }

    // ----------------------------------------- private helper methods -----------------------------------

    private StreamingResponseBody streamMedia(HttpServletRequest req, HttpServletResponse resp, FileInfo fi) throws IOException {
        final Enumeration<String> re = req.getHeaders(HttpHeaders.RANGE);
        MediaStreamingUtils.Segment segment = MediaStreamingUtils.parseRangeRequest(re.hasMoreElements() ? re.nextElement().trim() : null, fi.getSizeInBytes());
        /*
            at most 10mb, some browser like Chrome, always include range header 'range=0-' for the first request,
            which essentially request the whole file, it just doesn't make sense
         */
        if (segment.length() > SIZE_MB_10) {
            segment = new MediaStreamingUtils.Segment(segment.start, segment.start + (SIZE_MB_10) - 1);
        }

        // headers for range-request
        resp.setHeader(HttpHeaders.CONTENT_RANGE, String.format("bytes %d-%d/%d", segment.start, segment.end, fi.getSizeInBytes()));
        resp.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        // resp.setHeader(HttpHeaders.TRANSFER_ENCODING, "chunked");
        resp.setHeader(HttpHeaders.CONTENT_TYPE, "video/mp4");

        final long len = segment.length();
        resp.setHeader(HttpHeaders.CONTENT_LENGTH, Long.toString(len));

        // partial content 206
        resp.setStatus(HttpStatus.PARTIAL_CONTENT.value());

        // use FileChannel#transferTo to transfer the file
        return new ChannelStreamingResponseBody(fileInfoService.retrieveFileChannel(fi.getId()), fi.getName(), segment.start, len);
    }

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
