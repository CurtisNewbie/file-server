package com.yongj.vo;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.*;

/**
 * @author yongj.zhuang
 */
@Data
public class MoveFileIntoDirReqVo {

    @NotEmpty
    private String uuid;

    @Nullable
    private String parentFileUuid;
}
