package com.yongj.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.curtisnewbie.common.enums.IntEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum for file_info.is_logic_deleted
 *
 * @author yongjie.zhuang
 */
public enum FLogicDelete implements IntEnum {

    /** Normal */
    NORMAL(0),

    /** Logically deleted */
    DELETED(1);

    @JsonValue
    @EnumValue
    private final int value;

    FLogicDelete(int v) {
        this.value = v;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
