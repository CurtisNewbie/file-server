package com.yongj.io;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Handler of IO operation
 *
 * @author yongjie.zhuang
 */
@Validated
public interface IOHandler {

    /**
     * Check if a file exists
     */
    boolean exists(@NotEmpty String absPath);

    /**
     * Write data to a zip file of the absolute path
     */
    long writeZipFile(@NotEmpty String absPath, @NotEmpty List<ZipCompressEntry> entries) throws IOException;

    /**
     * Write data to a zip file of the absolute path
     */
    CompletableFuture<Long> writeZipFileAsync(@NotEmpty String absPath, @NotEmpty List<ZipCompressEntry> entries);

    /**
     * Write data (from an InputStream) to file of absolute path
     */
    long writeFile(@NotEmpty String absPath, @NotNull InputStream inputStream) throws IOException;

    /**
     * Write data (from an InputStream) to file of absolute path
     */
    CompletableFuture<Long> writeFileAsync(@NotEmpty String absPath, @NotNull InputStream inputStream);

    /**
     * Create the parent directory for given absolute path
     */
    void createParentDirIfNotExists(@NotEmpty String absPath) throws IOException;

    /**
     * Transfer data directly from {@code absPath} to {@code outputStream}
     *
     * @param absPath
     * @param outputStream
     */
    void readFile(@NotEmpty String absPath, @NotNull OutputStream outputStream) throws IOException;

    /**
     * Delete file
     *
     * @param absPath
     */
    void deleteFile(@NotEmpty String absPath) throws IOException;
}
