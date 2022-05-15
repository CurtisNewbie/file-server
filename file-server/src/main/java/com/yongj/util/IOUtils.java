package com.yongj.util;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * IOUtils
 *
 * @author yongj.zhuang
 */
public final class IOUtils {

    private IOUtils() {

    }

    /**
     * Copy data between channels, if {@code to} channel is a FileChannel, the data is appended at the end of the file
     *
     * @param from   from channel
     * @param to     to channel
     * @param buffer buffer
     */
    public static void copy(ReadableByteChannel from, WritableByteChannel to, ByteBuffer buffer) throws IOException {
        int p = 0;
        final boolean isFileChannel = to instanceof FileChannel;

        while (from.read(buffer) != -1) {
            ((Buffer) buffer).flip();
            while (buffer.hasRemaining()) {
                if (isFileChannel)
                    p += ((FileChannel) to).write(buffer, p);
                else
                    p += to.write(buffer);
            }
            ((Buffer) buffer).clear();
        }
    }
}
