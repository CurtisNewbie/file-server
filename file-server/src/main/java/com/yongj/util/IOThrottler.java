package com.yongj.util;

import com.curtisnewbie.common.util.ThreadUtils;

import static com.curtisnewbie.common.util.AssertUtils.greaterThanZero;

/**
 * IO Throttler
 * <p>
 * It's not thread-safe
 *
 * @author yongj.zhuang
 */
public class IOThrottler {

    /** Window size in milliseconds */
    private final long windowSize;

    /** throttle threshold in bytes */
    private final long throttleLimit;

    /** Throttle time in milliseconds */
    private final long throttleTime;

    /** Whether we should loop on throttling, if false, Thread.sleep is used instead */
    private final boolean loopOnThrottling;

    /** Current timestamp in milliseconds */
    private long timestamp = 0;

    /** Bytes sent within the window */
    private long bytesSent = 0;

    /**
     * Create IOThrottler
     *
     * @param windowSize        Window size in milliseconds
     * @param throttleThreshold throttle threshold in bytes
     */
    public IOThrottler(long windowSize, long throttleThreshold) {
        this(windowSize, throttleThreshold, 50, false);
    }

    /**
     * Create IOThrottler
     *
     * @param windowSize        Window size in milliseconds
     * @param throttleThreshold throttle threshold in bytes
     * @param throttleTime      throttle time in milliseconds
     * @param loopOnThrottling  loop on throttling or sleep on throttling
     */
    public IOThrottler(long windowSize, long throttleThreshold, long throttleTime, boolean loopOnThrottling) {
        greaterThanZero(windowSize, "windowLength <= 0");
        greaterThanZero(throttleThreshold, "windowSize <= 0");
        greaterThanZero(throttleTime, "throttleTime <= 0");
        this.windowSize = windowSize;
        this.throttleLimit = throttleThreshold;
        this.throttleTime = throttleTime;
        this.loopOnThrottling = loopOnThrottling;
    }

    public void throttleIfNecessary(long transferred) {
        // for the first time
        if (timestamp == 0) {
            timestamp = System.currentTimeMillis();
            bytesSent += transferred;
            return;
        }

        // next window
        final long now = System.currentTimeMillis();
        if (now > timestamp + windowSize) {
            timestamp = now;
            bytesSent = transferred;
            return;
        }

        // still in previous window, check whether we should throttle
        bytesSent += transferred;
        if (bytesSent >= throttleLimit) {
            waitTilNextWindow();
        }
    }

    private void waitTilNextWindow() {
        if (loopOnThrottling)
            ThreadUtils.loop(throttleTime);
        else
            ThreadUtils.sleep(throttleTime);
    }
}
