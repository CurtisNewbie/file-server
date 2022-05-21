package com.yongj.util;

import com.curtisnewbie.common.util.RandomUtils;

import java.util.Optional;

/**
 * Generator that only generate token consisted of numbers
 *
 * @author yongjie.zhuang
 */
public class NumberTokenGenerator implements TokenGenerator {
    private static final String PREFIX = "FE";
    private static final int DEFAULT_LENGTH = 15;

    @Override
    public String generate(Optional<Integer> length) {
        return RandomUtils.sequence(PREFIX, length.orElse(DEFAULT_LENGTH));
    }
}
