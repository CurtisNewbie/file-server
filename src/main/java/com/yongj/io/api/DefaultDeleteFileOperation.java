package com.yongj.io.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Default implementation of {@link DeleteFileOperation}
 *
 * @author yongjie.zhuang
 */
@Component
public class DefaultDeleteFileOperation implements DeleteFileOperation {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDeleteFileOperation.class);

    @Override
    public void deleteFile(String absPath) throws IOException {
        Objects.requireNonNull(absPath);

        Path p = Paths.get(absPath);
        File f = p.toFile();
        // if the file doesn't exist, we can also consider it as deleted already
        if (!f.exists())
            return;
        // does not support deleting directory
        if (f.isDirectory())
            throw new IOException("Not support deleting directory: " + absPath);


        if (!f.delete()) {
            throw new IOException("Cannot delete file" + absPath);
        }
        logger.info("Deleted file {}", absPath);
    }
}
