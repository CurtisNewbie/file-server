package com.yongj.io.operation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * Channel's implementation of  {@link WriteFileOperation}
 *
 * @author yongjie.zhuang
 */
public class ChannelWriteFileOperation implements WriteFileOperation {

    @Override
    public long writeFile(String absPath, InputStream inputStream) throws IOException {
        File file = new File(absPath);
        try (FileChannel fileChannel = new FileOutputStream(file).getChannel();
             ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream)) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
        return file.length();
    }
}
