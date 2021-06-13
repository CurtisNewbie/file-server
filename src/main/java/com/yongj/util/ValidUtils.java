package com.yongj.util;


import com.yongj.exceptions.ParamInvalidException;

import java.util.Objects;

/**
 * Validate Utility class,
 * <p>
 * For condition that does not match the rule, a {@code ParamInvalidException} is thrown
 * </p>
 *
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

    public static <T> void requireNonNull(T t, String errMsg) throws ParamInvalidException {
        if (t == null) {
            throw new ParamInvalidException(errMsg);
        }
    }

    public static <T, V> void requireEquals(T t, V v, String errMsg) throws ParamInvalidException {
        if (!Objects.equals(t, v)) {
            throw new ParamInvalidException(errMsg);
        }
    }
}
