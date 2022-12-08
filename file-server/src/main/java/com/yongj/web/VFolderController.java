package com.yongj.web;

import com.curtisnewbie.common.advice.RoleControlled;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.service.auth.messaging.helper.LogOperation;
import com.curtisnewbie.service.auth.remote.feign.UserServiceFeign;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByUserNosReq;
import com.curtisnewbie.service.auth.remote.vo.FetchUsernameByUserNosResp;
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

/**
 * @author yongj.zhuang
 */
@Slf4j
@RestController
@RequestMapping("${web.base-path}/vfolder")
public class VFolderController {

    @Autowired
    private VFolderService vFolderService;
    @Autowired
    private VFolderQueryService vFolderQueryService;
    @Autowired
    private UserServiceFeign userServiceFeign;

    @GetMapping("/brief/owned")
    public DeferredResult<Result<List<VFolderBrief>>> listOwnedVFolderBriefs() {
        return runAsyncResult(() -> vFolderQueryService.listOwnedVFolderBriefs(TraceUtils.requireUserNo()));
    }

    @PostMapping("/list")
    public DeferredResult<Result<PageableList<VFolderListResp>>> listVFolders(@RequestBody ListVFolderReq req) {
        final String userNo = TraceUtils.requireUserNo();
        log.info("List VFolders, req: {}, user: {}", req, userNo);

        req.setUserNo(userNo);
        return runAsyncResult(() -> vFolderQueryService.listVFolders(req));
    }

    @RoleControlled(rolesForbidden = "guest")
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

    @RoleControlled(rolesForbidden = "guest")
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

    @RoleControlled(rolesForbidden = "guest")
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

    @LogOperation(name = "shareVFolder", description = "Share access to vfolder")
    @RoleControlled(rolesForbidden = "guest")
    @PostMapping("/share")
    public DeferredResult<Result<Void>> shareVFolder(@RequestBody ShareVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Sharing VFolder, req: {}, user: {}", req, user.getUsername());

        return runAsync(() -> vFolderService.shareVFolder(ShareVFolderCmd.builder()
                .currUserNo(user.getUserNo())
                .sharedToUserNo(req.getUserNo())
                .folderNo(req.getFolderNo())
                .build()));
    }

    @RoleControlled(rolesForbidden = "guest")
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
            final FetchUsernameByUserNosResp fresp = Result.tryGetData(userServiceFeign.fetchUsernameByUserNos(freq), () -> "fetchUsernameByUserNos");
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


