package com.yongj.io.api;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.concurrent.Future;

/**
 * Handler of IO operation
 *
 * @author yongjie.zhuang
 */
public interface IOHandler {

    /**
     * Read file from absolute path asynchronously
     */
    Future<byte[]> asyncRead(@NotEmpty String absPath);

    /**
     * Writ data to file of absolute path asynchronously
     */
    void asyncWrite(@NotEmpty String absPath, @NotNull byte[] data);

    /**
     * Check if a file exists
     */
    boolean exists(@NotEmpty String absPath);
}
