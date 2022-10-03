package com.yongj.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * IOUtils
 *
 * @author yongj.zhuang
 */
public final class IOUtils {

    public static final ExecutorService ioThreadPool = Executors.newWorkStealingPool();

    private IOUtils() {

    }

    /**
     * Copy data between channels, if {@code to} channel is a FileChannel, the data is appended at the end of the file
     *
     * @param from   from channel
     * @param to     to channel
     * @param buffer buffer
     * @return number of bytes copied
     */
    public static long copy(ReadableByteChannel from, WritableByteChannel to, ByteBuffer buffer) throws IOException {
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
        return p;
    }

    /**
     * Copy data from FileChannel to another local file
     */
    public static void copy(FileChannel fromChannel, File toFile) throws IOException {
        try (FileChannel from = fromChannel;
             FileOutputStream fout = new FileOutputStream(toFile);
             FileChannel to = fout.getChannel()) {
            from.transferTo(0, Long.MAX_VALUE, to);
        }
    }
}
