package com.yongj.io;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

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
    long writeLocalZipFile(@NotEmpty String absPath, @NotEmpty List<File> entries) throws IOException;

    /**
     * Write data (from an InputStream) to file of absolute path
     */
    long writeFile(@NotEmpty String absPath, @NotNull InputStream inputStream) throws IOException;

    /**
     * Create the parent directory for given absolute path
     */
    void createParentDirIfNotExists(@NotEmpty String absPath) throws IOException;

    /**
     * Transfer data directly from {@code absPath} to {@code outputStream}
     *
     * @param absPath      absolute path
     * @param outputStream output stream
     */
    void readFile(@NotEmpty String absPath, @NotNull OutputStream outputStream) throws IOException;

    /**
     * Delete file
     *
     * @param absPath absolute path
     */
    void deleteFile(@NotEmpty String absPath) throws IOException;

    /** Obtain an input stream to the file */
    InputStream obtainInputStream(@NotEmpty String absPath) throws IOException;
}
