package com.yongj.enums;

import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.*;

/**
 * enum for file_info.user_group
 *
 * @author yongjie.zhuang
 */
public enum FileUserGroupEnum implements IntEnum {

    /** public group */
    PUBLIC(0),

    /** private group */
    PRIVATE(1);

    @JsonValue
    private final int value;

    FileUserGroupEnum(int v) {
        this.value = v;
    }

    @JsonCreator
    public static FileUserGroupEnum from(Integer userGroup) {
        if (userGroup == null) return null;
        return parse(userGroup);
    }

    public static FileUserGroupEnum parse(int userGroup) {
        return EnumUtils.parse(userGroup, FileUserGroupEnum.class);
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
