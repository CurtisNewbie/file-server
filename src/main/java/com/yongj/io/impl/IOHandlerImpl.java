package com.yongj.io.impl;

import com.yongj.io.api.IOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
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
    private int ioThreadNum;

    private ExecutorService executorService;

    @PostConstruct
    void init() {
        executorService = Executors.newFixedThreadPool(ioThreadNum);
        logger.info("[INIT] Using {} threads", ioThreadNum);
    }

    @Override
    public Future<byte[]> asyncRead(String absPath) {
        logger.info("Async read from '{}'", absPath);
        return executorService.submit(() -> {
            return Files.readAllBytes(Path.of(absPath));
        });
    }

    @Override
    public void asyncWrite(String absPath, byte[] data) {
        logger.info("Async write {} bytes to '{}'", data.length, absPath);
        executorService.execute(() -> {
            try {
                Files.write(Path.of(absPath), data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void asyncWriteWithStream(String absPath, InputStream inputStream) throws IOException {
        logger.info("Async write bytes from stream to '{}'", absPath);
        executorService.execute(() -> {
            File file = new File(absPath);
            try (FileChannel fileChannel = new FileOutputStream(file).getChannel();
                 ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream)) {
                fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
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
    public Future<Stream<Path>> asyncWalkDir(@NotEmpty String dir) {
        return executorService.submit(() -> {
            return Files.walk(Path.of(dir));
        });
    }
}
