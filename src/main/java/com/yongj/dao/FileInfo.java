package com.yongj.dao;

import java.util.Date;

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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public Integer getIsLogicDeleted() {
        return isLogicDeleted;
    }

    public void setIsLogicDeleted(Integer isLogicDeleted) {
        this.isLogicDeleted = isLogicDeleted;
    }

    public Integer getIsPhysicDeleted() {
        return isPhysicDeleted;
    }

    public void setIsPhysicDeleted(Integer isPhysicDeleted) {
        this.isPhysicDeleted = isPhysicDeleted;
    }

    public Integer getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(Integer uploaderId) {
        this.uploaderId = uploaderId;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    public Date getLogicDeleteTime() {
        return logicDeleteTime;
    }

    public void setLogicDeleteTime(Date logicDeleteTime) {
        this.logicDeleteTime = logicDeleteTime;
    }

    public Date getPhysicDeleteTime() {
        return physicDeleteTime;
    }

    public void setPhysicDeleteTime(Date physicDeleteTime) {
        this.physicDeleteTime = physicDeleteTime;
    }

    public Integer getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(Integer userGroup) {
        this.userGroup = userGroup;
    }
}