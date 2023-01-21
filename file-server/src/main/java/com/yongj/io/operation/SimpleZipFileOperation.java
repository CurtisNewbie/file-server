package com.yongj.io.operation;

import com.yongj.config.FileServiceConfig;
import com.yongj.io.ZipCompressEntry;
import com.yongj.util.IOThrottler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.yongj.util.IOUtils.copy;

/**
 * Simple implementation of {@link ZipFileOperation} using {@link java.util.zip.ZipOutputStream}
 *
 * @author yongjie.zhuang
 */
@Slf4j
public class SimpleZipFileOperation implements ZipFileOperation {

    private static final long HALF_SEC = 500;
    private static final long HALF_MB = 1024 * 512;

    @Autowired
    private FileServiceConfig fileServiceConfig;

    @Override
    public long compressFile(String absPath, List<ZipCompressEntry> entries) throws IOException {
        final File file = new File(absPath);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(8192 * 2);

        try (final ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
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

    @Override
    public long compressLocalFile(String absPath, List<File> entries) throws IOException {
        final boolean isIOLimited = fileServiceConfig.getCompressSpeedLimit() > -1;
        if (isIOLimited) {
            log.info("IOThrottler enabled, expected compression speed: {}mb/s", fileServiceConfig.getCompressSpeedLimit());
        }

        final IOThrottler ioThrottler = isIOLimited ? new IOThrottler(HALF_SEC, fileServiceConfig.getCompressSpeedLimit() * HALF_MB) : null;

        final File file = new File(absPath);
        final ByteBuffer buffer = ByteBuffer.allocateDirect(8192 * 2);

        try (final ZipOutputStream zipOut = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(file.toPath())));
             final WritableByteChannel wc = Channels.newChannel(zipOut)) {

            for (File entry : entries) {
                final ZipEntry ze = new ZipEntry(entry.getName());
                zipOut.putNextEntry(ze);
                try (final ReadableByteChannel rc = Channels.newChannel(Files.newInputStream(entry.toPath(), StandardOpenOption.READ))) {
                    copy(rc, wc, buffer, ioThrottler);
                }
                zipOut.closeEntry();
            }
        }
        return file.length();

    }
}
