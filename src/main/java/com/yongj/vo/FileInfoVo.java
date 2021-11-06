package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

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
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime uploadTime;

    /** size in bytes */
    private Long sizeInBytes;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** Whether current user is the owner of this file */
    private Boolean isOwner;
}
