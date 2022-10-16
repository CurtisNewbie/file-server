package com.yongj.web.streaming;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.util.StopWatch;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;

import static com.yongj.util.IOSpeedLogUtils.mbps;

/**
 * A Timed StreamingResponseBody
 *
 * @author yongjie.zhuang
 */
@Slf4j
public abstract class TimedStreamingResponseBody implements StreamingResponseBody {

    @Nullable
    protected final String fileName;
    protected final long pos;
    protected final long length;

    public TimedStreamingResponseBody(long pos, long length) {
        this(null, pos, length);
    }

    public TimedStreamingResponseBody(@Nullable String fileName, long pos, long length) {
        this.fileName = fileName != null ? fileName : "unknown";
        this.pos = pos;
        this.length = length;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        StopWatch sw = new StopWatch();

        log.info("Start downloading file: '{}', pos: {}, length: {}", fileName, pos, length);
        sw.start();
        final long transferred = timedWriteTo(outputStream, pos, length);
        sw.stop();

        final long totalTime = sw.getTotalTimeMillis();
        final String mbps = mbps(transferred, totalTime);
        final NumberFormat nf = NumberFormat.getInstance();
        log.info("Downloaded file: '{}', took {} ms, size: {} bytes, speed: {} mb/s, pos: {}", fileName, nf.format(totalTime),
                nf.format(transferred), mbps, pos);
    }

    /**
     * A timed and logged {@link #writeTo(OutputStream)} method
     *
     * @return number of bytes actually transferred
     */
    abstract long timedWriteTo(OutputStream outputStream, long pos, long length) throws IOException;

}
