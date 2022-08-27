package com.yongj.web;

import com.curtisnewbie.common.advice.*;
import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.vo.*;
import com.yongj.services.*;
import com.yongj.services.qry.*;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.*;

import static com.curtisnewbie.common.util.AsyncUtils.*;

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

    @PostMapping("/list")
    public DeferredResult<Result<PageableList<VFolderListResp>>> listVFolders(@RequestBody ListVFolderReq req) {
        final String userNo = TraceUtils.requireUserNo();
        log.info("List VFolders, req: {}, user: {}", req, userNo);

        req.setUserNo(userNo);
        return runAsyncResult(() -> vFolderQueryService.listVFolders(req));
    }

    @PostMapping("/file/list")
    public DeferredResult<Result<PageableList<ListFileInfoRespVo>>> listFilesInFolder(@RequestBody ListVFolderFilesReq req) {
        final String userNo = TraceUtils.requireUserNo();
        log.info("List files in VFolders, req: {}, user: {}", req, userNo);

        req.setUserNo(userNo);
        return runAsyncResult(() -> vFolderQueryService.listFilesInFolder(req));
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


