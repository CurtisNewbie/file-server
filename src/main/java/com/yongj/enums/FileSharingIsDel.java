package com.yongj.enums;

/**
 * Whether a file_sharing record is logically deleted
 *
 * @author yongjie.zhuang
 */
public enum FileSharingIsDel {

    TRUE(1),

    FALSE(0);

    private final int value;

    FileSharingIsDel(int v) {
        this.value = v;
    }

    public int getValue() {
        return value;
    }
}
