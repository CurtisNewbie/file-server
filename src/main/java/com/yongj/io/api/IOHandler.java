package com.yongj.io.api;

import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
    void asyncWriteWithChannel(@NotEmpty String absPath, @NotNull InputStream inputStream) throws IOException;

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
    Future<Resource> getFileResource(@NotEmpty String absPath);

    /**
     * Transfer data directly from {@code absPath} to {@code outputStream}
     *
     * @param absPath
     * @param outputStream
     */
    void transferByChannel(@NotEmpty String absPath, @NotNull OutputStream outputStream) throws IOException;
}
