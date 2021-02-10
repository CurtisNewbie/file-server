package com.yongj.io.api;

import org.redisson.api.RBucket;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
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
 * Manager of files inside the base dir, it internally uses redisson to handle cache. The way it uses redis cache is
 * basically that: 1. scan the whole directory periodically, 2. cache the file scanned and set a expiry time, 3. the
 * file that no longer exists will be expired. Thus, there may be a latency for the list of path being cached, but it
 * should provide decent performance.
 *
 * @author yongjie.zhuang
 */
@Component
public class FileManager {

    /** 10s */
    private static final int scanInterval = 10_000;
    private static final Logger logger = LoggerFactory.getLogger(FileManager.class);

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private IOHandler ioHandler;

    @Autowired
    private PathResolver pathResolver;

    @PostConstruct
    void init() {
        redisson.getKeys().deleteByPattern("*");
        logger.info("[INIT] FileManager setting scan interval: {} seconds", scanInterval / 1000);
    }


    /**
     * Cache a relative path
     *
     * @param relPath
     */
    public void cache(String relPath) {
        RBucket rBucket = redisson.getBucket(relPath);
        if (rBucket.isExists()) { // refresh expiry time if exists
            rBucket.expire(scanInterval, TimeUnit.MILLISECONDS);
        } else {
            rBucket.set(relPath);
        }
    }

    /**
     * Get all relative paths
     */
    public Iterable<String> getAll() {
        RKeys rKeys = redisson.getKeys();
        Iterable<String> keys = rKeys.getKeys(Integer.MAX_VALUE); // TODO, temporary solution
        return keys;
    }

    @Scheduled(fixedRate = scanInterval)
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
