package com.yongj.enums;

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

    private final int value;

    FileUserGroupEnum(int v) {
        this.value = v;
    }

    public static FileUserGroupEnum parseGroup(int userGroup) {
        for (FileUserGroupEnum e : FileUserGroupEnum.values()) {
            if (e.getValue() == userGroup)
                return e;
        }
        return null;
    }

    @Override
    public int getValue() {
        return this.value;
    }
}
