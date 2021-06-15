package com.yongj.enums;

/**
 * @author yongjie.zhuang
 */
public interface IntEnum<T extends Enum<T>> {

    int getValue();
}
