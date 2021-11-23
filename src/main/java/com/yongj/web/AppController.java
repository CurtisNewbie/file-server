package com.yongj.web;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.util.AuthUtil;
import com.curtisnewbie.service.auth.remote.api.RemoteUserAppService;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.curtisnewbie.service.auth.remote.vo.UserRequestAppApprovalCmd;
import com.yongj.config.SentinelFallbackConfig;
import com.yongj.vo.RequestAppApprovalWebVo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yongjie.zhuang
 */
@SentinelResource(value = "app", defaultFallback = "serviceNotAvailable", fallbackClass = SentinelFallbackConfig.class)
@RequestMapping("${web.base-path}/app")
public class AppController {

    @DubboReference
    private RemoteUserAppService remoteUserAppService;

    @PostMapping("/request-approval")
    public Result<Void> requestAppApproval(@Validated @RequestBody RequestAppApprovalWebVo reqVo) throws InvalidAuthenticationException {
        remoteUserAppService.requestAppUseApproval(UserRequestAppApprovalCmd.builder()
                .appId(reqVo.getAppId())
                .userId(AuthUtil.getUserId())
                .build());
        return Result.ok();
    }
}
