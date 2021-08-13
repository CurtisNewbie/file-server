package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.aop.LogOperation;
import com.curtisnewbie.service.auth.remote.api.RemoteAccessLogService;
import com.curtisnewbie.service.auth.remote.vo.AccessLogInfoVo;
import com.github.pagehelper.PageInfo;
import com.yongj.vo.AccessLogInfoFsVo;
import com.yongj.vo.ListAccessLogInfoReqVo;
import com.yongj.vo.ListAccessLogInfoRespVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/access")
public class AccessLogController {

    @DubboReference(lazy = true)
    private RemoteAccessLogService accessLogService;

    @LogOperation(name = "/access/history", description = "list access log info")
    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/history")
    public Result<ListAccessLogInfoRespVo> listAccessLogInfo(@RequestBody ListAccessLogInfoReqVo vo)
            throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getPagingVo());
        PageInfo<AccessLogInfoVo> pageInfo = accessLogService.findAccessLogInfoByPage(vo.getPagingVo());
        PagingVo paging = new PagingVo();
        paging.setTotal(pageInfo.getTotal());
        ListAccessLogInfoRespVo res = new ListAccessLogInfoRespVo(BeanCopyUtils.toTypeList(pageInfo.getList(), AccessLogInfoFsVo.class));
        res.setPagingVo(paging);
        return Result.of(res);
    }
}
