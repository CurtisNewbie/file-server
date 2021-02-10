package com.yongj.dto;

import java.io.Serializable;

/**
 * @author yongjie.zhuang
 */
public class Resp<T> implements Serializable {

    /** message being returned */
    private String msg;

    /** whether current response has an error */
    private boolean hasError;

    private T data;

    public static <T> Resp<T> empty() {
        var resp = new Resp<T>();
        resp.hasError = false;
        resp.msg = null;
        resp.data = null;
        return resp;
    }

    public static <T> Resp<T> of(T data) {
        var resp = new Resp<T>();
        resp.hasError = false;
        resp.msg = null;
        resp.data = data;
        return resp;
    }

    public static <T> Resp<T> error(String errMsg) {
        var resp = new Resp<T>();
        resp.hasError = true;
        resp.msg = errMsg;
        resp.data = null;
        return resp;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
