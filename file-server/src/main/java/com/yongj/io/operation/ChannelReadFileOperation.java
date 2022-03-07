package com.yongj.io.operation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Channel's implementation of {@link ReadFileOperation}
 *
 * @author yongjie.zhuang
 */
public class ChannelReadFileOperation implements ReadFileOperation {

    @Override
    public void readFile(String absPath, OutputStream outputStream) throws IOException {
        RandomAccessFile file = new RandomAccessFile(absPath, "r");
        try (FileChannel fChannel = file.getChannel();
             WritableByteChannel outChannel = Channels.newChannel(outputStream);) {
            fChannel.transferTo(0, Long.MAX_VALUE, outChannel);
        }
    }
}
