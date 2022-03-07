package com.yongj.web.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author yongjie.zhuang
 */
public class GzipStreamingResponseBody extends PlainStreamingResponseBody {

    public GzipStreamingResponseBody(InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(outputStream)) {
            int r;
            byte[] buffer = allocateBuffer();
            while ((r = in.read(buffer)) != -1) {
                gzipOut.write(buffer, 0, r);
            }
        }
    }
}
