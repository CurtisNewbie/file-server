package com.yongj.enums;

import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * @author yongjie.zhuang
 */
public enum FileExtensionIsEnabledEnum implements IntEnum {

    /** current file extension is enabled */
    ENABLED(0),
    /** current file extension is disabled */
    DISABLED(1);

    public final int value;

    FileExtensionIsEnabledEnum(int isEnabled) {
        this.value = isEnabled;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @JsonCreator
    public static FileExtensionIsEnabledEnum forValue(int v) {
        return EnumUtils.parse(v, FileExtensionIsEnabledEnum.class);
    }
}
