package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.aop.LogOperation;
import com.curtisnewbie.service.auth.remote.api.RemoteOperateLogService;
import com.curtisnewbie.service.auth.remote.vo.OperateLogVo;
import com.github.pagehelper.PageInfo;
import com.yongj.vo.FindOperateLogRespVo;
import com.yongj.vo.OperateLogFsVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/operate")
public class OperateLogController {

    @DubboReference
    private RemoteOperateLogService remoteOperateLogService;

    @LogOperation(name = "/operate/history", description = "find operate log history in pages", enabled = false)
    @PostMapping("/history")
    public Result<FindOperateLogRespVo> findByPage(@RequestBody PagingVo pagingVo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(pagingVo.getLimit());
        ValidUtils.requireNonNull(pagingVo.getTotal());

        PageInfo<OperateLogVo> pv = remoteOperateLogService.findOperateLogInfoInPages(pagingVo);
        return Result.of(toFindOperateLogRespVo(pv));
    }

    private FindOperateLogRespVo toFindOperateLogRespVo(PageInfo<OperateLogVo> pv) {
        PagingVo p = new PagingVo();
        p.setTotal(pv.getTotal());
        FindOperateLogRespVo res = new FindOperateLogRespVo();
        res.setPagingVo(p);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        res.setOperateLogVoList(pv.getList().stream().map(v -> {
            OperateLogFsVo fv = BeanCopyUtils.toType(v, OperateLogFsVo.class);
            fv.setOperateTime(sdf.format(v.getOperateTime()));
            return fv;
        }).collect(Collectors.toList()));
        return res;
    }
}
