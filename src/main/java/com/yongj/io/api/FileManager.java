package com.yongj.io.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
@Component
public class FileManager {

    /** 10s */
    private static final int SCAN_INTERVAL_MILLISEC = 10_000;
    private static final int MAXIMUM_SIZE = Integer.MAX_VALUE;
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);
    private static final Cache<String, String> PATH_CACHE = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(SCAN_INTERVAL_MILLISEC, TimeUnit.MILLISECONDS)
            .build();

    @Autowired
    private IOHandler ioHandler;

    @Autowired
    private PathResolver pathResolver;

    @PostConstruct
    void init() {
        logger.info("[INIT] FileManager setting scan interval: {} seconds", SCAN_INTERVAL_MILLISEC / 1000);
    }


    /**
     * Cache a relative path
     *
     * @param relPath
     */
    public void cache(String relPath) {
        PATH_CACHE.put(relPath, relPath);
    }

    /**
     * Get all relative paths
     */
    public Iterable<String> getAll() {
        return PATH_CACHE.asMap().values();
    }

    @Scheduled(fixedRate = SCAN_INTERVAL_MILLISEC)
    private void scanDir() {
        try {
            Stream<Path> absPathStream = ioHandler.scanDir(pathResolver.getBaseDir());
            List<String> relPaths = pathResolver.relativizePaths(absPathStream);
            relPaths.forEach(p -> {
                if (!p.isEmpty() && !p.endsWith(File.separator)) {
                    cache(p);
                }
            });
        } catch (IOException e) {
            logger.info("Scan base directory failed, will retry next time", e);
        }
    }
}
