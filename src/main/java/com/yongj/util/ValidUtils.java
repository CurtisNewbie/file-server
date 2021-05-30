package com.yongj.util;


import com.yongj.exceptions.ParamInvalidException;

/**
 * @author yongjie.zhuang
 */
public final class ValidUtils {

    private ValidUtils() {

    }

    public static <T> void requireNonNull(T t) throws ParamInvalidException {
        if (t == null) {
            throw new ParamInvalidException("Please enter required content");
        }
    }
}
