package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.github.pagehelper.PageInfo;
import com.yongj.enums.FsGroupMode;
import com.yongj.services.FsGroupService;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import com.yongj.vo.ListAllFsGroupRespVo;
import com.yongj.vo.UpdateFsGroupModeReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author yongjie.zhuang
 */
@RequestMapping("${web.base-path}/fsgroup")
@RestController
public class FsGroupController {

    @Autowired
    private FsGroupService fsGroupService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/mode/update")
    public Result<Void> updateFsGroupMode(@RequestBody UpdateFsGroupModeReqVo reqVo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(reqVo.getId());
        ValidUtils.requireNonNull(reqVo.getMode());
        FsGroupMode fgm = EnumUtils.parse(reqVo.getMode(), FsGroupMode.class);
        ValidUtils.requireNonNull(fgm, "fs_group mode value illegal");

        fsGroupService.updateFsGroupMode(reqVo.getId(), fgm);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/list")
    public Result<ListAllFsGroupRespVo> listAll(@RequestBody ListAllFsGroupReqVo reqVo) {
        PageInfo<FsGroupVo> pi = fsGroupService.findByPage(reqVo);
        ListAllFsGroupRespVo res = new ListAllFsGroupRespVo();
        PagingVo p = new PagingVo();
        p.setTotal(pi.getTotal());
        res.setPagingVo(p);
        res.setFsGroups(pi.getList());
        return Result.of(res);
    }
}
