package com.yongj.web;

import com.curtisnewbie.common.trace.TUser;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.vo.Result;
import com.yongj.services.VFolderService;
import com.yongj.vo.CreateVFolderCmd;
import com.yongj.vo.CreateVFolderReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yongj.zhuang
 */
@Slf4j
@RestController
@RequestMapping("${web.base-path}/vfolder")
public class VFolderController {

    @Autowired
    private VFolderService vFolderService;

    @PostMapping
    public Result<String> createVFolder(@RequestBody CreateVFolderReq req) {
        final TUser user = TraceUtils.tUser();
        log.info("Creating VFolder, req: {}, user: {}", req, user.getUsername());

        return Result.ofSupplied(() -> vFolderService.createVFolder(CreateVFolderCmd.builder()
                .name(req.getName())
                .username(user.getUsername())
                .userNo(user.getUserNo())
                .build()));
    }
}


