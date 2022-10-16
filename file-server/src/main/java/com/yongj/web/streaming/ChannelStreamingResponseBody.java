package com.yongj.web.streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Implementation of {@link StreamingResponseBody} based on FileChannel
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class ChannelStreamingResponseBody extends TimedStreamingResponseBody {

    private static final long CHUNK_SIZE = 16384L; //16kb
    private final FileChannel fileChannel;

    public ChannelStreamingResponseBody(FileChannel fileChannel, String fileName) throws IOException {
        super(fileName, 0, fileChannel.size());
        this.fileChannel = fileChannel;
    }

    public ChannelStreamingResponseBody(FileChannel fileChannel, String fileName, long pos, long length) {
        super(fileName, pos, length);
        this.fileChannel = fileChannel;
    }

    @Override
    long timedWriteTo(OutputStream outputStream, long pos, long length) throws IOException {
        long remaining = length;

        try (final WritableByteChannel wbc = Channels.newChannel(outputStream);
             FileChannel fc = fileChannel;) {

            while (remaining > 0) {
                if (Thread.interrupted()) {
                    Thread.currentThread().interrupt();
                    log.warn("ChannelStreamingResponseBody thread interrupted, aborting, filename: '{}', pos: {}", fileName, pos);
                    return length - remaining;
                }

                // chunkSize is for better responsiveness for thread interruption
                final long t = fc.transferTo(pos, Math.min(remaining, CHUNK_SIZE), wbc);
                pos += t;
                remaining -= t;
                log.debug("Transferred {} bytes to '{}', curr_pos: {}, target_length: {}", t, fileName, pos, length);
            }
        }
        return length;
    }

}
