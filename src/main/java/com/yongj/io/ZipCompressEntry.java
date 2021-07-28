package com.yongj.io;

import java.io.InputStream;
import java.io.Serializable;

/**
 * An entry in zip
 *
 * @author yongjie.zhuang
 */
public class ZipCompressEntry implements Serializable {

    /**
     * Entry name
     */
    private final String entryName;

    /**
     * InputStream
     */
    private final InputStream inputStream;

    public ZipCompressEntry(String entryName, InputStream inputStream) {
        this.entryName = entryName;
        this.inputStream = inputStream;
    }

    public String getEntryName() {
        return entryName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
