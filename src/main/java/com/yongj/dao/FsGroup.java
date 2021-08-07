package com.yongj.dao;

import lombok.Data;

/**
 * FileSystem group, used to differentiate which base folder or mounted folder should be used
 *
 * @author yongjie.zhuang
 */
@Data
public class FsGroup {

    private Integer id;

    /** group name */
    private String name;

    /** base folder */
    private String baseFolder;

    /** mode: 1-read, 2-read/write */
    private Integer mode;

}