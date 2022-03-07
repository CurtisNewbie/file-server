package com.yongj.util;

import java.security.SecureRandom;
import java.util.Optional;

/**
 * Generator that only generate token consisted of numbers
 *
 * @author yongjie.zhuang
 */
public class NumberTokenGenerator implements TokenGenerator {
    private final SecureRandom sr = new SecureRandom();
    private final int DEFAULT_LENGTH = 10;

    @Override
    public String generate(Optional<Integer> length) {
        int len;
        if (length.isPresent())
            len = length.get();
        else
            len = DEFAULT_LENGTH;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(sr.nextInt(10));
        }
        return sb.toString();
    }
}
