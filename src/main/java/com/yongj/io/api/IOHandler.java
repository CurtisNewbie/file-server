package com.yongj.io.api;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
     * Write data (from an InputStream) to file of absolute path asynchronously
     */
    void writeByChannel(@NotEmpty String absPath, @NotNull InputStream inputStream) throws IOException;

    /**
     * Transfer data directly from {@code absPath} to {@code outputStream}
     *
     * @param absPath
     * @param outputStream
     */
    void readByChannel(@NotEmpty String absPath, @NotNull OutputStream outputStream) throws IOException;
}
