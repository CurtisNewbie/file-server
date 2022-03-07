package com.yongj.enums;

import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;

/**
 * Ownership of the files being queried
 *
 * @author yongjie.zhuang
 */
public enum FileOwnership implements IntEnum {

    /** all the files */
    ALL_FILES(0),

    /** files that belong to the requester */
    FILES_OF_THE_REQUESTER(1);

    private final int val;

    FileOwnership(int v) {
        this.val = v;
    }

    @Override
    public int getValue() {
        return this.val;
    }

    public static FileOwnership parse(int v) {
        return EnumUtils.parse(v, FileOwnership.class);
    }
}
