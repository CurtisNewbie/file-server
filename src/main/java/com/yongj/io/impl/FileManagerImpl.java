package com.yongj.io.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yongj.io.api.FileManager;
import com.yongj.io.api.IOHandler;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author yongjie.zhuang
 */
@Component
public class FileManagerImpl implements FileManager {

    /** 10 seconds */
    private static final int SCAN_INTERVAL_MILLISEC = 10_000;
    /** Object stored inside cache's value, it doesn't have any practical meaning or usage */
    private static final EmptyObject EMPTY_OBJECT = new EmptyObject();
    private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

    /** Cache of relative paths to base directory */
    private final Cache<String, EmptyObject> REL_PATH_CACHE;

    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;

    public FileManagerImpl(@Value("${max.scanned.file.count}") long maxCacheSize) {
        logger.info("[INIT] Setting scan interval: {} seconds", SCAN_INTERVAL_MILLISEC / 1000);
        logger.info("[INIT] Setting cache's maximum size : {}", maxCacheSize);
        if (maxCacheSize <= 0 || maxCacheSize > Long.MAX_VALUE)
            throw new IllegalArgumentException("Cache's size should be greater than 0 and less than " + Long.MAX_VALUE);
        REL_PATH_CACHE = CacheBuilder.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterWrite(SCAN_INTERVAL_MILLISEC, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * Cache a relative path
     *
     * @param relPath
     */
    @Override
    public void cache(String relPath) {
        logger.debug("Cache relative path: '{}'", relPath);
        REL_PATH_CACHE.put(relPath, EMPTY_OBJECT);
    }

    @Override
    public Iterable<String> getAll() {
        logger.debug("Get all cached values");
        return new ArrayList<>(REL_PATH_CACHE.asMap().keySet());
    }

    @Scheduled(fixedRate = SCAN_INTERVAL_MILLISEC)
    protected void _scanDir() {
        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Start scanning base dir");
            stopWatch = new StopWatch();
            stopWatch.start();
        }
        try {
            Stream<Path> absPathStream = ioHandler.walkDir(pathResolver.getBaseDir());
            List<String> relPaths = pathResolver.relativizePaths(absPathStream);
            relPaths.forEach(p -> {
                if (!p.isEmpty() && !p.endsWith(File.separator)) {
                    cache(p);
                }
            });
        } catch (Exception ignored) {
            logger.warn("Scan base directory failed, will retry next time", ignored);
        }
        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("Finish scanning base dir, took {} millisec", stopWatch.getTotalTimeMillis());
        }
    }

    private static final class EmptyObject {
    }
}
