package com.yongj.vo;

/**
 * @author yongjie.zhuang
 */
public class RegisterUserVo {

    /**
     * username
     */
    private String username;

    /**
     * password (in plain text)
     */
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
