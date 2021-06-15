package com.yongj.enums;

/**
 * Enum for file_info.is_logic_deleted
 *
 * @author yongjie.zhuang
 */
public enum FileLogicDeletedEnum implements IntEnum<FileLogicDeletedEnum> {

    /** Normal */
    NORMAL(0),

    /** Logically deleted */
    LOGICALLY_DELETED(1);

    private final int value;

    FileLogicDeletedEnum(int v) {
        this.value = v;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
