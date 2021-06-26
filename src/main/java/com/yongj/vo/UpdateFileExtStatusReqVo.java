package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class UpdateFileExtStatusReqVo {

    /**
     * Id
     */
    private Long id;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    private Integer isEnabled;

}
