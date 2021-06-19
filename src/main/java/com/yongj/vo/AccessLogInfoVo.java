package com.yongj.vo;

import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class AccessLogInfoVo {

    /** when the user signed in */
    private String accessTime;

    /** ip address */
    private String ipAddress;

    /** username */
    private String username;

    /** primary key of user */
    private Integer userId;

    @Override
    public String toString() {
        return "AccessLogInfo{" +
                "accessTime=" + accessTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", username='" + username + '\'' +
                ", userId=" + userId +
                '}';
    }
}