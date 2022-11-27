package com.yongj.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Ownership of the files being queried
 *
 * @author yongjie.zhuang
 */
public enum FOwnership implements IntEnum {

    /** all the files */
    ALL_FILES(0),

    /** files that belong to the requester */
    FILES_OF_THE_REQUESTER(1);

    @JsonValue
    @EnumValue
    private final int val;

    FOwnership(int v) {
        this.val = v;
    }

    @Override
    public int getValue() {
        return this.val;
    }

    public static FOwnership parse(int v) {
        return EnumUtils.parse(v, FOwnership.class);
    }
}
