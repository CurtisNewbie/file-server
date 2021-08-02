package com.yongj.web.streaming;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Simple Implementation of {@link org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody}
 *
 * @author yongjie.zhuang
 */
public class PlainStreamingResponseBody implements StreamingResponseBody {

    protected static final int BUFFER_SIZE = 8192;
    protected final InputStream in;

    public PlainStreamingResponseBody(InputStream inputStream) {
        in = inputStream;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        int bytesRead;
        byte[] buffer = allocateBuffer();
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

    protected byte[] allocateBuffer() {
        return new byte[BUFFER_SIZE];
    }

}
