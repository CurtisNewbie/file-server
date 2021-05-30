package com.yongj.exceptions;


/**
 * Parameter invalid exception
 *
 * @author yongjie.zhuang
 */
public class ParamInvalidException extends Exception {

    private String msg;

    public ParamInvalidException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
