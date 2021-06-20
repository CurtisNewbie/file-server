package com.yongj.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Implementation of {@link DeleteFileOperation}, which move files to a temporary folder
 *
 * @author yongjie.zhuang
 */
public class TempFolderDeleteFileOperation implements DeleteFileOperation {

    private static final Logger logger = LoggerFactory.getLogger(TempFolderDeleteFileOperation.class);
    private static final String TEMP_FOLDER_NAME = "temp_deleted";

    @Autowired
    private PathResolver pathResolver;

    @Override
    public void deleteFile(String absPath) throws IOException {
        Objects.requireNonNull(absPath);

        Path p = Paths.get(absPath);
        File f = p.toFile();
        // if the file doesn't exist, we can also consider it as deleted already
        if (!f.exists()) {
            logger.info("File {} not exists, skipped", absPath);
            return;
        }
        // does not support deleting directory
        if (f.isDirectory())
            throw new IOException("Not support deleting directory: " + absPath);

        // move file to temporary folder
        Path fp = Paths.get(pathResolver.resolveFolder(TEMP_FOLDER_NAME));
        Files.move(p, fp);

        if (!f.delete()) {
            throw new IOException("Cannot delete file" + absPath);
        }
        logger.info("Deleted file {}", absPath);
    }
}
