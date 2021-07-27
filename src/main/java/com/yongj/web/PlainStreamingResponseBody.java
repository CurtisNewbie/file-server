package com.yongj.web;

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

    private static final int BUFFER_SIZE = 8192;
    private final InputStream in;

    public PlainStreamingResponseBody(InputStream inputStream) {
        in = inputStream;
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[BUFFER_SIZE];
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }
}
