package com.yongj.config;

import com.curtisnewbie.common.vo.Result;
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

    public static Result<Void> serviceNotAvailable(Throwable t) {
        log.error("Throttling", t);
        return Result.error("Server is busy, please try again later");
    }
}
