package com.yongj.io.impl;

import com.yongj.io.api.IOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author yongjie.zhuang
 */
@Service
public class IOHandlerImpl implements IOHandler {

    private static final Logger logger = LoggerFactory.getLogger(IOHandlerImpl.class);

    @Value("${thread.io.number}")
    private int ioThreads;

    private ExecutorService executorService;

    @PostConstruct
    void init() {
        executorService = Executors.newFixedThreadPool(ioThreads);
        logger.info("[INIT] IOHandler using {} threads", ioThreads);
    }

    @Override
    public Future<byte[]> asyncRead(String absPath) {
        return executorService.submit(() -> {
            return Files.readAllBytes(Path.of(absPath));
        });
    }

    @Override
    public void asyncWrite(String absPath, byte[] data) {
        executorService.execute(() -> {
            try {
                Files.write(Path.of(absPath), data, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean exists(String absPath) {
        return Files.exists(Path.of(absPath));
    }
}
