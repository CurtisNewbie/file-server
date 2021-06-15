package com.yongj.enums;

/**
 * @author yongjie.zhuang
 */
public enum FileExtensionIsEnabledEnum implements IntEnum<FileExtensionIsEnabledEnum> {

    /** current file extension is disabled */
    DISABLED(0),
    /** current file extension is enabled */
    ENABLED(1);

    public final int value;

    FileExtensionIsEnabledEnum(int isEnabled) {
        this.value = isEnabled;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
