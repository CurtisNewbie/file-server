package com.yongj.web.streaming;

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
public class ChannelStreamingResponseBody extends TimedStreamingResponseBody {

    private final FileChannel fileChannel;

    public ChannelStreamingResponseBody(FileChannel fileChannel, String fileName) {
        super(fileName);
        this.fileChannel = fileChannel;
    }

    @Override
    void timedWriteTo(OutputStream outputStream) throws IOException {
        try (final WritableByteChannel wbc = Channels.newChannel(outputStream)) {
            fileChannel.transferTo(0, Long.MAX_VALUE, wbc);
        }
    }

}
