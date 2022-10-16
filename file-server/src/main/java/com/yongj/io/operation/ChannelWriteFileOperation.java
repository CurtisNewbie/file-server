package com.yongj.io.operation;

import com.yongj.config.FileServiceConfig;
import com.yongj.util.IOThrottler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.StandardOpenOption;

import static com.curtisnewbie.common.util.AssertUtils.isTrue;
import static com.yongj.util.IOUtils.copy;

/**
 * Channel's implementation of  {@link WriteFileOperation}
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class ChannelWriteFileOperation extends TimedWriteFileOperation {

    private static final long HALF_MB = 1024 * 512;

    @Autowired
    private FileServiceConfig fileServiceConfig;

    @Override
    public long timedWriteFile(String absPath, InputStream inputStream) throws IOException {
        final File file = new File(absPath);
        isTrue(file.createNewFile(), "Failed to create new file");

        final boolean isIOLimited = fileServiceConfig.getUploadSpeedLimit() > -1;
        if (isIOLimited) {
            log.info("IOThrottler enabled, expected speed: {}mb/s", fileServiceConfig.getUploadSpeedLimit());
        }

        final IOThrottler ioThrottler = isIOLimited ? new IOThrottler(500, fileServiceConfig.getUploadSpeedLimit() * HALF_MB) : null;
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16384); // 16 * 1024

        try (final FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
             ReadableByteChannel rc = Channels.newChannel(inputStream)) {

            return copy(rc, fc, buffer, (transferred) -> {
                if (ioThrottler != null) ioThrottler.throttleIfNecessary(transferred);
            });
        }
    }
}
