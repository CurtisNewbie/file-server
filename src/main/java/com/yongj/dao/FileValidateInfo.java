package com.yongj.dao;

public class FileValidateInfo {

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    private Integer isLogicDeleted;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    public Integer getIsLogicDeleted() {
        return isLogicDeleted;
    }

    public void setIsLogicDeleted(Integer isLogicDeleted) {
        this.isLogicDeleted = isLogicDeleted;
    }

    public Integer getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Integer uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Integer getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Integer userGroup) {
        this.userGroup = userGroup;
    }
}