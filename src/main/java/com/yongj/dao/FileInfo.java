package com.yongj.dao;

import java.util.Date;

/**
 * FileInfo entity
 *
 * @author yongjie.zhuang
 */
public class FileInfo {
    private Integer id;

    /** name of the file */
    private String name;

    /** file's relative path */
    private String relPath;

    /** file's uuid */
    private String uuid;

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    private Byte isLogicDeleted;

    /** whether the file is physically deleted, 0-normal, 1-deleted */
    private Byte isPhysicDeleted;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** upload time */
    private Date uploadTime;

    /** when the file is logically deleted */
    private Date logicDeleteTime;

    /** when the file is physically deleted */
    private Date physicDeleteTime;

    /** the group that the file belongs to, either 'public' or 'private' */
    private String userGroup;

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

    public String getRelPath() {
        return relPath;
    }

    public void setRelPath(String relPath) {
        this.relPath = relPath == null ? null : relPath.trim();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid == null ? null : uuid.trim();
    }

    public Byte getIsLogicDeleted() {
        return isLogicDeleted;
    }

    public void setIsLogicDeleted(Byte isLogicDeleted) {
        this.isLogicDeleted = isLogicDeleted;
    }

    public Byte getIsPhysicDeleted() {
        return isPhysicDeleted;
    }

    public void setIsPhysicDeleted(Byte isPhysicDeleted) {
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

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup == null ? null : userGroup.trim();
    }
}