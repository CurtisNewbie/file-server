package com.yongj.enums;

import com.curtisnewbie.common.enums.IntEnum;

/**
 * Mode of a fs_group
 *
 * @author yongjie.zhuang
 */
public enum FsGroupMode implements IntEnum {

    /** 1 read-only */
    READ(1),

    /** 2 read/write */
    READ_WRITE(2);

    private final int v;

    FsGroupMode(int v) {
        this.v = v;
    }

    @Override
    public int getValue() {
        return v;
    }
}
