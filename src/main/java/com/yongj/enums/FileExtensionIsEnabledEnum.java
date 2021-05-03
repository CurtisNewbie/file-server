package com.yongj.enums;

/**
 * @author yongjie.zhuang
 */
public enum FileExtensionIsEnabledEnum {

    /** current file extension is disabled */
    DISABLED(0),
    /** current file extension is enabled */
    ENABLED(1);

    public final int value;

    FileExtensionIsEnabledEnum(int isEnabled) {
        this.value = isEnabled;
    }
}
