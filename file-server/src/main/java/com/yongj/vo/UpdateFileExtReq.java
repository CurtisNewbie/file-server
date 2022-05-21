package com.yongj.vo;

import com.yongj.enums.FExtIsEnabled;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class UpdateFileExtReq {

    /**
     * primary key
     */
    private Integer id;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    private FExtIsEnabled isEnabled;
}
