package com.yongj.io.impl;

import com.yongj.io.api.IOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

/**
 * @author yongjie.zhuang
 */
@Component
public class IOHandlerImpl implements IOHandler {

    private static final Logger logger = LoggerFactory.getLogger(IOHandlerImpl.class);

    @Value("${io.thread.number}")
    private int ioThreads;

    private ExecutorService executorService;

    @PostConstruct
    void init() {
        executorService = Executors.newFixedThreadPool(ioThreads);
        logger.info("[INIT] IOHandler using {} threads", ioThreads);
    }

    @Override
    public Future<byte[]> asyncRead(String absPath) {
        logger.info("Async read from {}", absPath);
        return executorService.submit(() -> {
            return Files.readAllBytes(Path.of(absPath));
        });
    }

    @Override
    public void asyncWrite(String absPath, byte[] data) {
        logger.info("Async write to {}", absPath);
        executorService.execute(() -> {
            try {
                Files.write(Path.of(absPath), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean exists(String absPath) {
        return Files.exists(Path.of(absPath));
    }

    @Override
    public Stream<Path> scanDir(String dir) throws IOException {
        return Files.walk(Path.of(dir));
    }
}
