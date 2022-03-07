package com.yongj.util;

import java.util.Optional;

/**
 * Generator of token
 *
 * @author yongjie.zhuang
 */
public interface TokenGenerator {

    /**
     * Generate token
     *
     * @param length (optional)
     * @return token
     */
    String generate(Optional<Integer> length);
}
