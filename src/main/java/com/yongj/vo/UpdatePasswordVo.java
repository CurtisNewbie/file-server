package com.yongj.vo;

/**
 * @author yongjie.zhuang
 */
public class UpdatePasswordVo {

    /**
     * Previous password
     */
    private String prevPassword;

    /**
     * New password
     */
    private String newPassword;

    public String getPrevPassword() {
        return prevPassword;
    }

    public void setPrevPassword(String prevPassword) {
        this.prevPassword = prevPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
