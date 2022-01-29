package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.aop.LogOperation;
import com.yongj.enums.FsGroupMode;
import com.yongj.services.FsGroupService;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import com.yongj.vo.ListAllFsGroupRespVo;
import com.yongj.vo.UpdateFsGroupModeReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
@RequestMapping("${web.base-path}/fsgroup")
@RestController
public class FsGroupController {

    @Autowired
    private FsGroupService fsGroupService;

    @LogOperation(name = "/fsgroup/mode/update", description = "update fsgroup mode")
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/mode/update")
    public Result<Void> updateFsGroupMode(@RequestBody UpdateFsGroupModeReqVo reqVo) throws MsgEmbeddedException {
        reqVo.validate();

        FsGroupMode fgm = EnumUtils.parse(reqVo.getMode(), FsGroupMode.class);
        ValidUtils.requireNonNull(fgm, "fs_group mode value illegal");

        fsGroupService.updateFsGroupMode(reqVo.getId(), fgm);
        return Result.ok();
    }

    @LogOperation(name = "/fsgroup/list", description = "list fsgroup")
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/list")
    public Result<ListAllFsGroupRespVo> listAll(@RequestBody ListAllFsGroupReqVo reqVo) throws MsgEmbeddedException {
        reqVo.validate();

        PageablePayloadSingleton<List<FsGroupVo>> pi = fsGroupService.findByPage(reqVo);
        ListAllFsGroupRespVo res = new ListAllFsGroupRespVo(pi.getPayload());
        res.setPagingVo(pi.getPagingVo());
        return Result.of(res);
    }
}
