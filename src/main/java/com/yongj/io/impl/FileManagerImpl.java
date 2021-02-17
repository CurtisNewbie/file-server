package com.yongj.io.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yongj.io.api.FileManager;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

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
@Component
public class FileManagerImpl implements FileManager {

    /** 10 seconds */
    private static final int SCAN_INTERVAL_MILLISEC = 10_000;
    private static final int MAXIMUM_SIZE = Integer.MAX_VALUE;
    private static final Logger logger = LoggerFactory.getLogger(com.yongj.io.api.FileManager.class);
    private static final Cache<String, String> REL_PATH_CACHE = CacheBuilder.newBuilder()
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
    @Override
    public void cache(String relPath) {
        logger.debug("Cache relative path: '{}'", relPath);
        REL_PATH_CACHE.put(relPath, relPath);
    }

    /**
     * Get all relative paths
     */
    @Override
    public Iterable<String> getAll() {
        logger.debug("Get all cached values");
        Iterable<String> relPaths = REL_PATH_CACHE.asMap().values();
        return relPaths;
    }

    @Scheduled(fixedRate = SCAN_INTERVAL_MILLISEC)
    protected void _scanDir() {
        logger.debug("Start scanning base dir");
        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }
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
        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("Finish scanning base dir, took {} millisec", stopWatch.getTotalTimeMillis());
        }
    }
}
