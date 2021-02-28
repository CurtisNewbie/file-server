package com.yongj.io.api;

import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * Handler of IO operation
 *
 * @author yongjie.zhuang
 */
@Validated
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

    /**
     * Write data (from an InputStream) to file of absolute path asynchronously
     */
    void asyncWriteWithChannel(String absPath, InputStream inputStream) throws IOException;

    /**
     * Scan/walk the directory asynchronously
     *
     * @param dir
     * @return A stream of Path under the directory
     * @throws IOException
     */
    Future<Stream<Path>> asyncWalkDir(@NotEmpty String dir);

    /**
     * Get Resource of the file
     *
     * @param absPath absolute path
     * @return Resource
     */
    Future<Resource> getFileResource(String absPath);
}
