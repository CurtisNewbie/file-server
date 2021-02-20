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

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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
    /** Object stored inside cache's value, it doesn't have any practical meaning or usage */
    private static final EmptyObject IN_CACHE = new EmptyObject();
    private static final Logger logger = LoggerFactory.getLogger(FileManagerImpl.class);

    /** Maximum size of the cache */
    @Value("${max.scanned.file.count}")
    private long MAX_CACHE_SIZE;

    /** Cache of relative paths to base directory */
    private Cache<String, EmptyObject> REL_PATH_CACHE;

    @Autowired
    private IOHandler ioHandler;

    @Autowired
    private PathResolver pathResolver;

    @PostConstruct
    void init() {
        logger.info("[INIT] Setting scan interval: {} seconds", SCAN_INTERVAL_MILLISEC / 1000);
        logger.info("[INIT] Setting cache's maximum size : {}", MAX_CACHE_SIZE);
        if (MAX_CACHE_SIZE <= 0 || MAX_CACHE_SIZE > Long.MAX_VALUE)
            throw new IllegalArgumentException("Cache's size should be greater than 0 and less than " + Long.MAX_VALUE);
        REL_PATH_CACHE = CacheBuilder.newBuilder()
                .maximumSize(MAX_CACHE_SIZE)
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
        REL_PATH_CACHE.put(relPath, IN_CACHE);
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
            Future<Stream<Path>> absPathStreamFuture = ioHandler.asyncWalkDir(pathResolver.getBaseDir());
            Stream<Path> absPathStream = absPathStreamFuture.get();
            List<String> relPaths = pathResolver.relativizePaths(absPathStream);
            relPaths.forEach(p -> {
                if (!p.isEmpty() && !p.endsWith(File.separator)) {
                    cache(p);
                }
            });
        } catch (InterruptedException | ExecutionException e) {
            logger.warn("Scan base directory failed, will retry next time", e);
        }
        if (logger.isDebugEnabled()) {
            stopWatch.stop();
            logger.debug("Finish scanning base dir, took {} millisec", stopWatch.getTotalTimeMillis());
        }
    }

    private static final class EmptyObject {
    }
}
