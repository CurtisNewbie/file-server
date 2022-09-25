package com.yongj.vo;

import com.yongj.enums.FileType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author yongjie.zhuang
 */
@Data
public class FileInfoVo {

    /** id */
    private Integer id;

    /** file's uuid */
    private String uuid;

    /** name of the file */
    private String name;

    /** upload time */
    private LocalDateTime uploadTime;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** uploader's name */
    private String uploaderName;

    /** size in bytes */
    private Long sizeInBytes;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** Whether current user is the owner of this file */
    private Boolean isOwner;

    /** file type */
    private FileType fileType;

    private LocalDateTime updateTime;

    public void checkAndSetIsOwner(int currentUserId) {
        this.isOwner = Objects.equals(uploaderId, currentUserId);
    }
}
