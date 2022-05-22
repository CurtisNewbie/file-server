package com.yongj.web.streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;

import static com.yongj.util.IOSpeedLogUtils.*;

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
        long len = 0L;
        try {
            sw.start();
            len = timedWriteTo(outputStream);
        } finally {
            sw.stop();
            final long totalMillisec = sw.getTotalTimeMillis();
            final String mbps = mbps(len, totalMillisec);
            final NumberFormat nf = NumberFormat.getInstance();
            log.info("Downloaded file: '{}', took {} ms, size: {} bytes, speed: {} mb/s", fileName, nf.format(totalMillisec), nf.format(len), mbps);
        }
    }

    /**
     * A timed and logged {@link #writeTo(OutputStream)} method
     *
     * @return number of bytes actually transferred
     */
    abstract long timedWriteTo(OutputStream outputStream) throws IOException;

}
