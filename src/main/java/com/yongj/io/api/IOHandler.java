package com.yongj.io.api;

import java.util.concurrent.Future;

/**
 * Handler of IO operation
 *
 * @author yongjie.zhuang
 */
public interface IOHandler {

    /**
     * Read file from relative path asynchronously
     */
    Future<byte[]> asyncRead(String relPath);

    /**
     * Writ data to file of relative path asynchronously
     */
    void asyncWrite(String relPath, byte[] data);

    /**
     * Check if a file exists
     */
    boolean exists(String relPath);
}
