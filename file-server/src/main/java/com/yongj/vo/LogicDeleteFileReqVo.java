package com.yongj.vo;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * @author yongjie.zhuang
 */
@Data
public class LogicDeleteFileReqVo {

    /** file key */
    @NotEmpty
    private String uuid;

}
