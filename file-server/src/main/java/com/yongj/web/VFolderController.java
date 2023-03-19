package com.yongj.web;

import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.goauth.client.PathDoc;
import com.curtisnewbie.service.auth.messaging.helper.LogOperation;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByUserNosReq;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByUserNosResp;
import com.curtisnewbie.service.auth.remote.vo.FindUserReq;
import com.curtisnewbie.service.auth.remote.vo.UserInfoVo;
import com.yongj.services.VFolderService;
import com.yongj.services.qry.VFolderQueryService;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.curtisnewbie.common.util.AsyncUtils.runAsync;
import static com.curtisnewbie.common.util.AsyncUtils.runAsyncResult;
import static com.curtisnewbie.common.vo.Result.tryGetData;

/**
 * @author yongj.zhuang
 */
@Slf4j
@RestController
@PathDoc(resourceCode = Resources.MANAGE_FILE_CODE, resourceName = Resources.MANAGE_FILE_NAME)
@RequestMapping("${web.base-path}/vfolder")
public class VFolderController {

    @Autowired
    private VFolderService vFolderService;
    @Autowired
    private VFolderQueryService vFolderQueryService;
    @Autowired
    private UserServiceFeign userServiceFeign;

    @PathDoc(description = "User list virtual folder briefs")
    @GetMapping("/brief/owned")
    public DeferredResult<Result<List<VFolderBrief>>> listOwnedVFolderBriefs() {
        return runAsyncResult(() -> vFolderQueryService.listOwnedVFolderBriefs(TraceUtils.requireUserNo()));
    }

    @PathDoc(description = "User list virtual folders")
    @PostMapping("/list")
    public DeferredResult<Result<PageableList<VFolderListResp>>> listVFolders(@RequestBody ListVFolderReq req) {
        final String userNo = TraceUtils.requireUserNo();
        log.info("List VFolders, req: {}, user: {}", req, userNo);

        req.setUserNo(userNo);
        return runAsyncResult(() -> vFolderQueryService.listVFolders(req));
    }

    @PathDoc(description = "Create virtual folder")
    @PostMapping("/create")
    public DeferredResult<Result<String>> createVFolder(@RequestBody CreateVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Creating VFolder, req: {}, user: {}", req, user.getUsername());

        return runAsyncResult(() -> vFolderService.createVFolder(CreateVFolderCmd.builder()
                .name(req.getName())
                .username(user.getUsername())
                .userNo(user.getUserNo())
                .build()));
    }

    @PathDoc(description = "Add file to virtual folder")
    @PostMapping("/file/add")
    public DeferredResult<Result<Void>> addFileToVFolder(@RequestBody AddFileToVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Adding files to VFolder, req: {}, user: {}", req, user.getUsername());

        return runAsync(() -> vFolderService.addFileToVFolder(AddFileToVFolderCmd.builder()
                .userNo(user.getUserNo())
                .folderNo(req.getFolderNo())
                .fileKeys(req.getFileKeys())
                .build()));
    }

    @PathDoc(description = "Remove file from virtual folder")
    @PostMapping("/file/remove")
    public DeferredResult<Result<Void>> removeFileFromVFolder(@RequestBody RemoveFileFromVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Removing files from VFolder, req: {}, user: {}", req, user.getUsername());

        return runAsync(() -> vFolderService.removeFileFromVFolder(RemoveFileFromVFolderCmd.builder()
                .userNo(user.getUserNo())
                .folderNo(req.getFolderNo())
                .fileKeys(req.getFileKeys())
                .build()));
    }

    @PathDoc(description = "Share access to virtual folder")
    @LogOperation(name = "shareVFolder", description = "Share access to vfolder")
    @PostMapping("/share")
    public DeferredResult<Result<Void>> shareVFolder(@RequestBody ShareVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Sharing VFolder, req: {}, user: {}", req, user.getUsername());

        final UserInfoVo uv = tryGetData(userServiceFeign.findUser(FindUserReq.builder()
                .username(req.getUsername())
                .build()));
        AssertUtils.nonNull(uv, "User '%s' doesn't exist", req.getUsername());

        return runAsync(() -> vFolderService.shareVFolder(ShareVFolderCmd.builder()
                .currUserNo(user.getUserNo())
                .sharedToUserNo(uv.getUserNo())
                .folderNo(req.getFolderNo())
                .build()));
    }

    @PathDoc(description = "Remove granted access to virtual folder")
    @LogOperation(name = "removeGrantedFolderAccess", description = "Remove granted access to vfolder")
    @PostMapping("/access/remove")
    public DeferredResult<Result<Void>> removeGrantedFolderAccess(@RequestBody RemoveGrantedFolderAccessReq req) {
        final String userNo = TraceUtils.requireUserNo();
        return runAsync(() -> vFolderService.removeGrantedAccess(RemoveGrantedVFolderAccessCmd.builder()
                .currUserNo(userNo)
                .folderNo(req.getFolderNo())
                .sharedToUserNo(req.getUserNo())
                .build()));
    }

    @PathDoc(description = "List granted access to virtual folder")
    @PostMapping("/granted/list")
    public DeferredResult<Result<PageableList<GrantedFolderAccess>>> listGrantedFolderAccess(@RequestBody ListGrantedFolderAccessReq req) {
        final String userNo = TraceUtils.requireUserNo();
        return runAsyncResult(() -> {
            final PageableList<GrantedFolderAccess> resp = vFolderQueryService.listGrantedAccess(req, userNo);
            if (resp.getPayload().isEmpty()) return resp;

            var userNos = resp.getPayload().stream()
                    .map(GrantedFolderAccess::getUserNo)
                    .distinct()
                    .collect(Collectors.toList());

            var freq = new FetchUsernameByUserNosReq(userNos);
            final FetchUsernameByUserNosResp fresp = tryGetData(userServiceFeign.fetchUsernameByUserNos(freq), () -> "fetchUsernameByUserNos");
            if (fresp.getUserNoToUsername() != null) {
                final Map<String, String> userNoToName = fresp.getUserNoToUsername();
                resp.getPayload().forEach(gfa -> {
                    if (userNoToName.containsKey(gfa.getUserNo()))
                        gfa.setUsername(userNoToName.get(gfa.getUserNo()));
                });
            }
            return resp;
        });
    }
}


