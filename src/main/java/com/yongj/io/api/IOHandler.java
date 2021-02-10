package com.yongj.io.api;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.stream.Stream;

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

    /** scan the dir
     * @return*/
    Stream<Path> scanDir(String path) throws IOException;
}
