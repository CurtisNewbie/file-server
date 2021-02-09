package com.yongj.exceptions;

/**
 * Exception indicating that the given path is illegal, which might be resulted by illegal characters.
 *
 * @author yongjie.zhuang
 */
public class IllegalPathException extends IllegalArgumentException {

    public IllegalPathException(String s) {
        super(s);
    }

    public IllegalPathException(Throwable t) {
        super(t);
    }
}
