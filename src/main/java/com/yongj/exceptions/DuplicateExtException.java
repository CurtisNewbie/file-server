package com.yongj.exceptions;

/**
 * Exception indicating the file extension with same name exists already
 *
 * @author yongjie.zhuang
 */
public class DuplicateExtException extends IllegalArgumentException {

    public DuplicateExtException(String e) {
        super(e);
    }

    public DuplicateExtException(Throwable t) {
        super(t);
    }

}
