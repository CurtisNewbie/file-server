package com.yongj.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.curtisnewbie.common.enums.IntEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for file_info.is_physic_deleted
 *
 * @author yongjie.zhuang
 */
public enum FPhysicDelete implements IntEnum {

    /** Normal */
    NORMAL(0),

    /** Physically deleted */
    DELETED(1);

    @JsonValue
    @EnumValue
    private final int value;

    FPhysicDelete(int v) {
        this.value = v;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
