package com.yongj.util;

import org.springframework.lang.Nullable;

/**
 * Generator of token
 *
 * @author yongjie.zhuang
 */
public interface TokenGenerator {

    /**
     * Generate token
     *
     * @param length
     * @return token
     */
    String generate(@Nullable Integer length);
}
