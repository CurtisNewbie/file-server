package com.yongj.enums;

/**
 * FileTask status
 *
 * @author yongj.zhuang
 */
public enum FileTaskStatus {
    INIT(false),
    PROCESSING(false),
    FINISHED(true),
    INTERRUPTED(true),
    FAILED(true);

    public final boolean isEndState;

    FileTaskStatus(boolean isEndState) {
        this.isEndState = isEndState;
    }
}
