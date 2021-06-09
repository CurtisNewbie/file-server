package com.yongj.vo;

/**
 * @author yongjie.zhuang
 */
public class UserVo {

    private Integer id;

    /**
     * username
     */
    private String username;

    /**
     * role
     */
    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
