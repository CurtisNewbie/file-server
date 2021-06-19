package com.yongj.enums;

/**
 * An Enum with an int value assigned
 *
 * @param <T> The Enum type that implements this IntEnum
 * @author yongjie.zhuang
 */
public interface IntEnum<T extends Enum<T>> {

    /**
     * Get int value
     */
    int getValue();
}
