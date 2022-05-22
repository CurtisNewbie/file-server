package com.yongj.io.operation;

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
public class ChannelWriteFileOperation extends TimedWriteFileOperation {

    @Override
    public long timedWriteFile(String absPath, InputStream inputStream) throws IOException {
        final File file = new File(absPath);
        isTrue(file.createNewFile(), "Failed to create new file");

        final ByteBuffer buffer = ByteBuffer.allocateDirect(65536); // 64 * 1024

        try (final FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
             ReadableByteChannel rc = Channels.newChannel(inputStream)) {

            return copy(rc, fc, buffer);
        }
    }
}
