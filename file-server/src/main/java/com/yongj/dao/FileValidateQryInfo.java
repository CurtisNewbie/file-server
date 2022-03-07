package com.yongj.dao;

import lombok.Data;

@Data
public class FileValidateQryInfo {

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    private Integer isLogicDeleted;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;
}