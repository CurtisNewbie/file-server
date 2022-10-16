package com.yongj.io.operation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;

import static com.yongj.util.IOSpeedLogUtils.mbps;

/**
 * Abstract implementation that time the WriteFileOperation
 *
 * @author yongj.zhuang
 */
@Slf4j
public abstract class TimedWriteFileOperation implements WriteFileOperation {

    @Override
    public long writeFile(String absPath, InputStream inputStream) throws IOException {
        StopWatch sw = new StopWatch();
        sw.start();
        log.info("Writing file to '{}'", absPath);
        final long len = timedWriteFile(absPath, inputStream);
        sw.stop();
        final long totalMillisec = sw.getTotalTimeMillis();
        final String mbps = mbps(len, totalMillisec);
        final NumberFormat nf = NumberFormat.getInstance();
        log.info("Wrote file to '{}', took {} ms, size: {} bytes, speed: {} mb/s", absPath, nf.format(totalMillisec),
                nf.format(len), mbps);
        return len;
    }

    abstract protected long timedWriteFile(String absPath, InputStream inputStream) throws IOException;
}
