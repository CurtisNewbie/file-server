package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yongj.enums.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileInfoWebVo {

    /** id */
    private Integer id;

    /** file's uuid */
    private String uuid;

    /** name of the file */
    private String name;

    /** name of the uploader */
    private String uploaderName;

    /** upload time */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime uploadTime;

    /** size in bytes */
    private Long sizeInBytes;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** Whether current user is the owner of this file */
    private Boolean isOwner;

    /** file type: FILE, DIR */
    private FileType fileType;

    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime updateTime;

}
