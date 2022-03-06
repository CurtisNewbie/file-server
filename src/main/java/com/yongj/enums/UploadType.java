package com.yongj.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * Upload type
 *
 * @author yongjie.zhuang
 */
@Getter
public enum UploadType {

    /** Uploaded by user */
    USER_UPLOADED(0),

    /** Uploaded by other applications / services */
    APP_UPLOADED(1);

    @EnumValue
    private final int value;

    UploadType(int value) {
        this.value = value;
    }
}
