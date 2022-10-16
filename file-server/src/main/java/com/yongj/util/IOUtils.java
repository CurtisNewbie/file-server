package com.yongj.util;

import com.curtisnewbie.common.data.LongWrapper;
import com.curtisnewbie.common.util.Runner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

/**
 * IOUtils
 *
 * @author yongj.zhuang
 */
@Slf4j
public final class IOUtils {

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
        return copy(from, to, buffer, null);
    }

    /**
     * Copy data between channels, if {@code to} channel is a FileChannel, the data is appended at the end of the file
     *
     * @param from      from channel
     * @param to        to channel
     * @param buffer    buffer
     * @param throttler throttler (nullable)
     * @return number of bytes copied
     */
    public static long copy(ReadableByteChannel from, WritableByteChannel to, ByteBuffer buffer,
                            @Nullable Consumer<Long /* bytesTransferred */> throttler) throws IOException {
        long p = 0;
        final boolean isFileChannel = to instanceof FileChannel;

        while (from.read(buffer) != -1) {
            ((Buffer) buffer).flip();
            while (buffer.hasRemaining()) {
                long t;
                if (isFileChannel)
                    t = ((FileChannel) to).write(buffer, p);
                else
                    t = to.write(buffer);
                p += t;

                if (throttler != null) throttler.accept(t);
            }
            ((Buffer) buffer).clear();
        }
        return p;
    }

    /**
     * Copy data from FileChannel to another local file
     */
    public static void copy(FileChannel fromChannel, File toFile) throws IOException {
        final long size = fromChannel.size();
        long transferred = 0L;
        long pos = 0L;

        try (FileChannel from = fromChannel;
             FileOutputStream fout = new FileOutputStream(toFile);
             FileChannel to = fout.getChannel()) {
            while (transferred < size) {
                long t = from.transferTo(pos, Long.MAX_VALUE, to);
                transferred += t;
                pos += t;
            }
        }
    }

    /**
     * Copy data from FileChannel to the given OutputStream
     *
     * @return number of bytes transferred
     */
    public static long copy(FileChannel fileChannel, OutputStream outputStream, long pos, long length) throws IOException {
        try (final WritableByteChannel wbc = Channels.newChannel(outputStream)) {
            return copy(fileChannel, wbc, pos, length);
        }
    }

    /**
     * Copy data from FileChannel to the given WritableByteChannel
     *
     * @return number of bytes transferred
     */
    public static long copy(FileChannel fileChannel, WritableByteChannel wbc, long pos, long length) throws IOException {
        long transferred = 0L;
        while (transferred < length) {
            final long t = fileChannel.transferTo(pos, length, wbc);
            transferred += t;
            pos += t;
        }
        return transferred;
    }

    /**
     * Calculate size of a directory by walking the file tree
     */
    public static long sizeOfDir(Path path) {
        final LongWrapper size = new LongWrapper(0);
        Runner.tryRun(() -> {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    size.incrBy(attrs.size());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        });
        return size.getValue();
    }
}
