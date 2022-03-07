package com.yongj.exceptions;

/**
 * Exception indicating the file extension is illegal
 *
 * @author yongjie.zhuang
 */
public class IllegalExtException extends IllegalArgumentException {

    public IllegalExtException(String e) {
        super(e);
    }

    public IllegalExtException(Throwable t) {
        super(t);
    }

}
