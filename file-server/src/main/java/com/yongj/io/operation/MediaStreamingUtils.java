package com.yongj.io.operation;

import lombok.ToString;

/**
 * Utils for media streaming
 *
 * @author yongj.zhuang
 */
public class MediaStreamingUtils {

    /** Parse HTTP Range Request header, return Segment [0-Long.MAX_VALUE] if rangeHeader is null */
    public static Segment parseRangeRequest(String rangeHeader, long fileSizeInBytes) {
        if (rangeHeader == null) return new Segment(0, Segment.INF);

        // partial content, range specified, e.g., bytes = 123-124
        long start = 0, end = fileSizeInBytes - 1;
        final String[] eqSplit = rangeHeader.split("=");
        if (eqSplit.length > 0) {
            // todo fix this, it may also use suffix as well :D
            final String[] ranges = eqSplit[1].split("-");
            final int rl = ranges.length;
            if (rl > 0) {
                start = Long.parseLong(ranges[0].trim());
                if (rl > 1) end = Long.parseLong(ranges[1].trim());
            }
        }
        return new Segment(start, end);
    }

    /**
     * Segment
     */
    @ToString
    public static class Segment {
        public static final long INF = Long.MAX_VALUE;
        public final long start;
        public final long end;

        public Segment(long start, long end) {
            this.start = start;
            this.end = end;
        }

        public long length() {
            return end - start + 1;
        }
    }
}
