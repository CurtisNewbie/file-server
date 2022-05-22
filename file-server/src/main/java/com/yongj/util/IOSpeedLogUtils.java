package com.yongj.util;

/**
 * Utils for logging I/O speed
 *
 * @author yongj.zhuang
 */
public final class IOSpeedLogUtils {

    private IOSpeedLogUtils() {
    }

    public static double mb(long bytes) {
        return bytes / 1024d / 1024d;
    }

    public static double sec(long millisec) {
        return millisec / 1000d;
    }

    /**
     * MB per sec as string
     */
    public static String mbps(long bytes, long milliseconds) {
        return String.format("%.2f", mb(bytes) / sec(milliseconds));
    }

}
