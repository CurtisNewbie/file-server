package com.yongj.config;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Generic fallback handler for sentinel
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class SentinelFallbackConfig {

    public static Result<Void> serviceNotAvailable(Throwable t) throws Throwable {
        if (t instanceof MsgEmbeddedException || t instanceof InvalidAuthenticationException)
            throw t;
        log.error("Fallback method invoked: ", t);
        return Result.error("Server is busy, please try again later");
    }
}
