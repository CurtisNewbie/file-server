package com.yongj.io.impl;

import com.yongj.io.api.IOHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author yongjie.zhuang
 */
@Component
public class IOHandlerImpl implements IOHandler {

    private static final Logger logger = LoggerFactory.getLogger(IOHandlerImpl.class);

    @Override
    public long writeByChannel(String absPath, InputStream inputStream) throws IOException {
        File file = new File(absPath);
        try (FileChannel fileChannel = new FileOutputStream(file).getChannel();
             ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream)) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return file.length();
    }

    @Override
    public boolean exists(String absPath) {
        return Files.exists(Path.of(absPath));
    }

    @Override
    public void readByChannel(@NotEmpty String absPath, @NotNull OutputStream outputStream) throws IOException {
        RandomAccessFile file = new RandomAccessFile(absPath, "r");
        try (var fChannel = file.getChannel()) {
            fChannel.transferTo(0, fChannel.size(), Channels.newChannel(outputStream));
        }
    }
}
