package com.yongj.web;

import com.curtisnewbie.common.advice.RoleControlled;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.service.auth.messaging.helper.LogOperation;
import com.yongj.services.FsGroupService;
import com.yongj.vo.AddFsGroupReq;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import com.yongj.vo.UpdateFsGroupModeReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import static com.curtisnewbie.common.util.AsyncUtils.runAsync;
import static com.curtisnewbie.common.util.AsyncUtils.runAsyncResult;

/**
 * @author yongjie.zhuang
 */
@RoleControlled(rolesRequired = "admin")
@RequestMapping("${web.base-path}/fsgroup")
@RestController
public class FsGroupController {

    @Autowired
    private FsGroupService fsGroupService;

    @LogOperation(name = "updateFsGroupMode", description = "Update FsGroup Mode (READ/READ_WRITE)")
    @PostMapping("/mode/update")
    public DeferredResult<Result<Void>> updateFsGroupMode(@RequestBody UpdateFsGroupModeReqVo reqVo) {
        return runAsync(() -> fsGroupService.updateFsGroupMode(reqVo.getId(), reqVo.getMode(), TraceUtils.tUser().getUsername()));
    }

    @RoleControlled(rolesRequired = "admin")
    @LogOperation(name = "addFsGroup", description = "Add FsGroup")
    @PostMapping("/add")
    public DeferredResult<Result<Void>> addFsGroup(@RequestBody AddFsGroupReq req) {
        return runAsync(() -> fsGroupService.addFsGroup(req));
    }

    @PostMapping("/list")
    public DeferredResult<Result<PageableList<FsGroupVo>>> listAll(@RequestBody ListAllFsGroupReqVo reqVo) {
        return runAsyncResult(() -> fsGroupService.findByPage(reqVo));
    }
}
