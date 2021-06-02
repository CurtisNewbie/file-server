package com.yongj.io.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.yongj.config.PathConfig;
import com.yongj.dto.FileInfo;
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

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yongjie.zhuang
 */
@Component
public class FileManagerImpl implements FileManager {

    /** 10 seconds */
    private static final int SCAN_INTERVAL_MILLI_SECONDS = 10_000;
    private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

    /** Cache of relative paths to base directory */
    private final Cache<String, Long> REL_PATH_CACHE;

    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private PathConfig pathConfig;

    public FileManagerImpl(@Value("${max.scanned.file.count}") long maxCacheSize) {
        logger.info("[INIT] Setting scan interval: {} seconds", SCAN_INTERVAL_MILLI_SECONDS / 1000);
        logger.info("[INIT] Setting cache's maximum size : {}", maxCacheSize);
        if (maxCacheSize <= 0 || maxCacheSize > Long.MAX_VALUE)
            throw new IllegalArgumentException("Cache's size should be greater than 0 and less than " + Long.MAX_VALUE);
        REL_PATH_CACHE = CacheBuilder.newBuilder()
                .maximumSize(maxCacheSize)
                .expireAfterWrite(SCAN_INTERVAL_MILLI_SECONDS, TimeUnit.MILLISECONDS)
                .build();
    }

    /**
     * Cache a relative path
     *
     * @param relPath
     */
    @Override
    public void cache(String relPath, long sizeInBytes) {
        logger.debug("Cache relative path: '{}'", relPath);
        REL_PATH_CACHE.put(relPath, sizeInBytes);
    }

    @Override
    public void cache(@NotEmpty String relPath) {
        logger.debug("Cache relative path: '{}'", relPath);
        Path p = Paths.get(pathConfig.getBasePath(), relPath);
        try {
            REL_PATH_CACHE.put(relPath, Files.size(p));
        } catch (IOException e) {
            logger.error("Failed to cache file: {}", p);
        }
    }

    @Override
    public Iterable<FileInfo> getAll() {
        logger.debug("Get all cached values");
        return new ArrayList<>(REL_PATH_CACHE.asMap().entrySet()
                .stream()
                .map(e -> new FileInfo(e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }

    @Scheduled(fixedRate = SCAN_INTERVAL_MILLI_SECONDS)
    protected void _scanDir() {
        StopWatch stopWatch = null;
        if (logger.isDebugEnabled()) {
            logger.debug("Start scanning base dir");
            stopWatch = new StopWatch();
            stopWatch.start();
        }
        try {
            Stream<Path> absPathStream = ioHandler.walkDir(pathConfig.getBasePath());
            absPathStream.forEach(p -> {
                String ps = pathResolver.relativizePath(p);
                if (!ps.isEmpty() && !ps.endsWith(File.separator)) {
                    try {
                        cache(ps, Files.size(p));
                    } catch (IOException e) {
                        logger.error("Failed to cache file: {}", ps);
                    }
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
}
