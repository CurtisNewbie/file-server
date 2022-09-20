package com.yongj.web;

import com.curtisnewbie.common.advice.RoleControlled;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.yongj.services.VFolderService;
import com.yongj.services.qry.VFolderQueryService;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

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
        log.info("Adding file to VFolder, req: {}, user: {}", req, user.getUsername());

        return runAsync(() -> vFolderService.addFileToVFolder(AddFileToVFolderCmd.builder()
                .userNo(user.getUserNo())
                .folderNo(req.getFolderNo())
                .fileKeys(req.getFileKeys())
                .build()));
    }
}


