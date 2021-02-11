package com.yongj.io.api;

/**
 * Manager of files inside the base dir, it internally uses redisson to handle cache.
 *
 * <p>
 * The way it uses redis cache is basically that:
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
public interface FileManager {

    /**
     * Cache a relative path
     *
     * @param relPath
     */
    void cache(String relPath);

    /**
     * Get all relative paths
     */
    Iterable<String> getAll();
}
