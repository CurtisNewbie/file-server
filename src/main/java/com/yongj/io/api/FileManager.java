package com.yongj.io.api;

import com.yongj.dto.FileInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Manager of files inside the base dir, it internally uses Guava's Cache to handle cache.
 *
 * <p>
 * The way it uses cache is basically that:
 * <li>
 * 1. scan the whole directory periodically,
 * </li>
 * <li>
 * 2. cache the file scanned and set a expiry time (if the file path is already cached, refresh the expiry time),
 * </li>
 * <li>
 * 3. the file that no longer exists will be expired.
 * </li>
 * <p>
 * Thus, there may be a latency for the list of path being cached, but it should provide acceptable performance.
 *
 * @author yongjie.zhuang
 */
@Validated
public interface FileManager {

    /**
     * Cache a relative path
     *
     * @param relPath
     * @param sizeInBytes
     */
    void cache(@NotEmpty String relPath, long sizeInBytes);

    /**
     * Cache a relative path
     *
     * @param relPath
     */
    void cache(@NotEmpty String relPath);

    /**
     * Get all cached file infos
     * <p>
     * This method obtains a deep copy of all the relative paths in cache, so the returned Iterable is not backed by any
     * other data structure
     */
    Iterable<FileInfo> getAll();
}
