package com.yongj.dao;

import lombok.Data;

import java.util.Date;

@Data
public class FileInfo {

    private Integer id;

    /** name of the file */
    private String name;

    /** file's uuid */
    private String uuid;

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    private Integer isLogicDeleted;

    /** whether the file is physically deleted, 0-normal, 1-deleted */
    private Integer isPhysicDeleted;

    /** size of file in bytes */
    private Long sizeInBytes;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** upload time */
    private Date uploadTime;

    /** when the file is logically deleted */
    private Date logicDeleteTime;

    /** when the file is physically deleted */
    private Date physicDeleteTime;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;
}