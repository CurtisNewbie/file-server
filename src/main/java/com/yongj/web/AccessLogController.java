package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.vo.AccessLogInfoVo;
import com.curtisnewbie.module.auth.services.api.AccessLogService;
import com.github.pagehelper.PageInfo;
import com.yongj.vo.AccessLogInfoFsVo;
import com.yongj.vo.ListAccessLogInfoReqVo;
import com.yongj.vo.ListAccessLogInfoRespVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/access")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/history")
    public Result<ListAccessLogInfoRespVo> listAccessLogInfo(@RequestBody ListAccessLogInfoReqVo vo)
            throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getPagingVo());
        PageInfo<AccessLogInfoVo> pageInfo = accessLogService.findAccessLogInfoByPage(vo.getPagingVo());
        PagingVo paging = new PagingVo();
        paging.setTotal(pageInfo.getTotal());
        return Result.of(
                new ListAccessLogInfoRespVo(
                        toAccessLogInfoVoList(pageInfo.getList()), paging
                )
        );
    }

    private static List<AccessLogInfoFsVo> toAccessLogInfoVoList(List<AccessLogInfoVo> list) {
        return list.stream().map(d -> {
            AccessLogInfoFsVo v = BeanCopyUtils.toType(d, AccessLogInfoFsVo.class);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            v.setAccessTime(sdf.format(d.getAccessTime()));
            return v;
        }).collect(Collectors.toList());
    }
}
