package com.yongj.web;

import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.vo.*;
import com.yongj.services.*;
import com.yongj.services.qry.*;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    private FileService fileService;

    @PostMapping("/list")
    public Result<PageableList<VFolderListResp>> listVFolders(@RequestBody ListVFolderReq req) {
        final String userNo = TraceUtils.requireUserNo();
        log.info("List VFolders, req: {}, user: {}", req, userNo);

        req.setUserNo(userNo);
        return Result.of(vFolderQueryService.listVFolders(req));
    }

    @PostMapping("/file/list")
    public Result<PageableList<ListFileInfoRespVo>> listFilesInFolder(@RequestBody ListVFolderFilesReq req) {
        final String userNo = TraceUtils.requireUserNo();
        log.info("List VFolders, req: {}, user: {}", req, userNo);

        req.setUserNo(userNo);
        return Result.of(fileService.listFilesInFolder(req));
    }

    @PostMapping("/create")
    public Result<String> createVFolder(@RequestBody CreateVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Creating VFolder, req: {}, user: {}", req, user.getUsername());

        return Result.ofSupplied(() -> vFolderService.createVFolder(CreateVFolderCmd.builder()
                .name(req.getName())
                .username(user.getUsername())
                .userNo(user.getUserNo())
                .build()));
    }

    @PostMapping("/file/add")
    public Result<Void> addFileToVFolder(@RequestBody AddFileToVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Adding file to VFolder, req: {}, user: {}", req, user.getUsername());

        return Result.runThenOk(() -> vFolderService.addFileToVFolder(AddFileToVFolderCmd.builder()
                .userNo(user.getUserNo())
                .folderNo(req.getFolderNo())
                .fileKey(req.getFileKey())
                .build()));
    }
}


