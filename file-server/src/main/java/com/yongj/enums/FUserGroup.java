package com.yongj.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.curtisnewbie.common.enums.IntEnum;
import com.curtisnewbie.common.util.EnumUtils;
import com.fasterxml.jackson.annotation.*;

/**
 * enum for file_info.user_group
 *
 * @author yongjie.zhuang
 */
public enum FUserGroup implements IntEnum {

    /** public group */
    PUBLIC(0),

    /** private group */
    PRIVATE(1);

    @EnumValue
    @JsonValue
    private final int value;

    FUserGroup(int v) {
        this.value = v;
    }

    @JsonCreator
    public static FUserGroup from(Integer userGroup) {
        if (userGroup == null) return null;
        return parse(userGroup);
    }

    public static FUserGroup parse(int userGroup) {
        return EnumUtils.parse(userGroup, FUserGroup.class);
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
