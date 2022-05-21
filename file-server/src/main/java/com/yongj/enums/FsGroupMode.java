package com.yongj.enums;

import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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

    @JsonValue
    private final int v;

    FsGroupMode(int v) {
        this.v = v;
    }

    @Override
    public int getValue() {
        return v;
    }

    @JsonCreator
    public static FsGroupMode from(Integer v) {
        return EnumUtils.parse(v, FsGroupMode.class);
    }
}
