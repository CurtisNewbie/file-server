package com.yongj.dao;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileExtension {

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
    private Integer isEnabled;
}
