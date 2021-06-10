package com.yongj.enums;

/**
 * Enum for file_info.is_physic_deleted
 *
 * @author yongjie.zhuang
 */
public enum FilePhysicDeletedEnum implements IntEnum {

    /** Normal */
    NORMAL(0),

    /** Physically deleted */
    PHYSICALLY_DELETED(1);

    private final int value;

    FilePhysicDeletedEnum(int v) {
        this.value = v;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
