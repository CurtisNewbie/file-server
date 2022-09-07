package com.yongj.vo;

import lombok.*;

import javax.validation.constraints.*;

/**
 * @author yongj.zhuang
 */
@Data
public class MoveFileIntoDirReqVo {

    @NotEmpty
    private String uuid;

    @NotEmpty
    private String parentFileUuid;
}
