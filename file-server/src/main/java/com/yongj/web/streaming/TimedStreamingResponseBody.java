package com.yongj.web.streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A Timed StreamingResponseBody
 *
 * @author yongjie.zhuang
 */
@Slf4j
public abstract class TimedStreamingResponseBody implements StreamingResponseBody {

    @Nullable
    private final String fileName;

    public TimedStreamingResponseBody() {
        this(null);
    }

    public TimedStreamingResponseBody(@Nullable String fileName) {
        this.fileName = fileName != null ? fileName : "unknown";
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        StopWatch sw = new StopWatch();
        try {
            sw.start();
            timedWriteTo(outputStream);
        } finally {
            log.info("Downloaded file: '{}' took {} ms", fileName, sw.getTotalTimeMillis());
            sw.stop();
        }
    }

    abstract void timedWriteTo(OutputStream outputStream) throws IOException;

}
