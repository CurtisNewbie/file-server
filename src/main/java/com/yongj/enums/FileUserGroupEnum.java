package com.yongj.enums;

import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;

/**
 * enum for file_info.user_group
 *
 * @author yongjie.zhuang
 */
public enum FileUserGroupEnum implements IntEnum<FileUserGroupEnum> {

    /** public group */
    PUBLIC(0),

    /** private group */
    PRIVATE(1);

    private final int value;

    FileUserGroupEnum(int v) {
        this.value = v;
    }

    public static FileUserGroupEnum parse(int userGroup) {
        return EnumUtils.parse(userGroup, FileUserGroupEnum.class);
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
