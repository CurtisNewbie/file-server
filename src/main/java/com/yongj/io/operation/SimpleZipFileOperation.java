package com.yongj.io.operation;

import com.yongj.io.ZipCompressEntry;
import org.springframework.stereotype.Component;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Simple implementation of {@link ZipFileOperation} using {@link java.util.zip.ZipOutputStream}
 *
 * @author yongjie.zhuang
 */
@Component
public class SimpleZipFileOperation implements ZipFileOperation {

    private static final int BUFFER_SIZE = 8192;

    @Override
    public long compressFile(String absPath, List<ZipCompressEntry> entries) throws IOException {
        File file = new File(absPath);

        byte[] buffer = new byte[BUFFER_SIZE];

        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));) {
            for (ZipCompressEntry entry : entries) {
                ZipEntry ze = new ZipEntry(entry.getEntryName());
                zipOut.putNextEntry(ze);

                try (BufferedInputStream inputStream = new BufferedInputStream(entry.getInputStream())) {
                    int size;
                    while ((size = inputStream.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, size);
                    }
                    zipOut.flush();
                }
            }
        }
        return file.length();
    }
}
