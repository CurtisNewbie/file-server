package com.yongj.vo;

import com.yongj.enums.FExtIsEnabled;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileExtVo {

    /**
     * primary key
     */
    private Integer id;

    /**
     * name of file extension, e.g., "txt"
     */
    private String name;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    private FExtIsEnabled isEnabled;

    private String createdBy;

    private String updatedBy;
}
