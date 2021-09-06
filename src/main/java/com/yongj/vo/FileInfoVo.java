package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileInfoVo {

    /**
     * id
     */
    private Integer id;

    /**
     * UUID
     */
    private String uuid;

    /**
     * fileName
     */
    private String name;

    /** upload time */
    private String uploadTime;

    /**
     * size in bytes
     */
    private Long sizeInBytes;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** Whether current user is the owner of this file */
    private Boolean isOwner;
}
