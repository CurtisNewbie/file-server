package com.yongj.io.operation;

import com.yongj.io.ZipCompressEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.yongj.util.IOUtils.copy;

/**
 * Simple implementation of {@link ZipFileOperation} using {@link java.util.zip.ZipOutputStream}
 *
 * @author yongjie.zhuang
 */
public class SimpleZipFileOperation implements ZipFileOperation {

    @Override
    public long compressFile(String absPath, List<ZipCompressEntry> entries) throws IOException {
        final File file = new File(absPath);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(8192 * 2);

        try (final ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
             final WritableByteChannel wc = Channels.newChannel(zipOut)) {

            for (ZipCompressEntry entry : entries) {
                final ZipEntry ze = new ZipEntry(entry.getEntryName());
                zipOut.putNextEntry(ze);
                try (final ReadableByteChannel rc = Channels.newChannel(entry.getInputStream())) {
                    copy(rc, wc, buffer);
                }
                zipOut.closeEntry();
            }
        }
        return file.length();
    }
}
