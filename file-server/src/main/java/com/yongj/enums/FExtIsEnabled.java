package com.yongj.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author yongjie.zhuang
 */
public enum FExtIsEnabled implements IntEnum {

    /** current file extension is enabled */
    ENABLED(0),
    /** current file extension is disabled */
    DISABLED(1);

    @JsonValue
    @EnumValue
    public final int value;

    FExtIsEnabled(int isEnabled) {
        this.value = isEnabled;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @JsonCreator
    public static FExtIsEnabled forValue(int v) {
        return EnumUtils.parse(v, FExtIsEnabled.class);
    }
}
