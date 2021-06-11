package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileInfoVo {

    /**
     * UUID
     */
    private String uuid;

    /**
     * fileName
     */
    private String name;

    /**
     * size in bytes
     */
    private Long sizeInBytes;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;
}
