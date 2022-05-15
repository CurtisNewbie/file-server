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
public class ChannelWriteFileOperation implements WriteFileOperation {

    @Override
    public long writeFile(String absPath, InputStream inputStream) throws IOException {
        final File file = new File(absPath);
        isTrue(file.createNewFile(), "Failed to create new file");

        final ByteBuffer buffer = ByteBuffer.allocateDirect(8192 * 2);

        try (final FileChannel fc = FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.APPEND);
             ReadableByteChannel rc = Channels.newChannel(inputStream)) {

            copy(rc, fc, buffer);
        }
        return file.length();
    }
}
