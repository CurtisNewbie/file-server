package com.yongj.file.remote.vo;

import lombok.Data;

/**
 * @author yongj.zhuang
 */
@Data
public class FileInfoResp {

    /** name of the file */
    private String name;

    /** file's uuid */
    private String uuid;

    /** size of file in bytes */
    private Long sizeInBytes;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** uploader name */
    private String uploaderName;

    /** when the file is deleted */
    private boolean isDeleted;

    /** file type: FILE, DIR */
    private String fileType;

    /** parent file's uuid */
    private String parentFile;
}
