package com.yongj.vo;

import com.yongj.enums.FsGroupType;
import lombok.Data;

/**
 * FileSystem group, used to differentiate which base folder or mounted folder should be used
 *
 * @author yongjie.zhuang
 */
@Data
public class FsGroupVo {

    private Integer id;

    /** group name */
    private String name;

    /** base folder */
    private String baseFolder;

    /** mode: 1-read, 2-read/write */
    private Integer mode;

    /** Type of a fs_group */
    private FsGroupType type;

}