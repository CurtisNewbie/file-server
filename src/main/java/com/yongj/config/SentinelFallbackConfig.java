package com.yongj.config;

import com.curtisnewbie.common.vo.Result;

/**
 * <p>
 * Generic fallback handler for sentinel
 * </p>
 *
 * @author yongjie.zhuang
 */
public class SentinelFallbackConfig {

    public static Result<Void> serviceNotAvailable() {
        return Result.error("Server is busy, please try again later");
    }
}
