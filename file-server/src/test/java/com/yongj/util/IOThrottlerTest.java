package com.yongj.util;

import com.curtisnewbie.common.util.StopWatchUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.NumberFormat;

import static com.yongj.util.IOSpeedLogUtils.mbps;

@Slf4j
public class IOThrottlerTest {

    @Test
    public void should_throttle_io() {
        final long total = 1_048_576_000; // 1gb
        final long chunkSize = 4096; // for each transferring


        IOThrottler ioThrottler = new IOThrottler(500, 26214400); // 50mb/s
        final long totalMillisec = StopWatchUtils.stopwatch(() -> {

            long remaining = total;
            while (remaining > 0) {
                // mimic file transferring
                ioThrottler.throttleIfNecessary(chunkSize);

                remaining -= chunkSize;
            }
        });

        final String mbps = mbps(total, totalMillisec);
        final NumberFormat nf = NumberFormat.getInstance();
        System.out.printf("Took %s ms, size: %s bytes, speed: %s mb/s", nf.format(totalMillisec), nf.format(total), mbps);
    }

}