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
 * Implementation of {@link DeleteFileOperation}
 * <p>
 * This implementation moves files to a temporary folder, see {@link #TEMP_FOLDER_NAME}
 * </p>
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

        Path filePath = Paths.get(absPath);
        File file = filePath.toFile();
        // if the file doesn't exist, we can also consider it as deleted already
        if (!file.exists()) {
            logger.info("File {} not exists, skipped", absPath);
            return;
        }
        // does not support deleting directory
        if (file.isDirectory())
            throw new IOException("Not support moving directory: " + absPath);

        Path folderPath = Paths.get(pathResolver.resolveFolder(TEMP_FOLDER_NAME));
        // create folder if not exists
        if (!Files.exists(folderPath)) {
            Files.createDirectory(folderPath);
        }
        // create the target file, which is under the temp folder
        File targetFile = new File(folderPath.toFile(), file.getName());
        // move file to temporary folder
        Files.move(filePath, targetFile.toPath());
        logger.info("Moved file {} to {}", absPath, targetFile.getAbsolutePath());
    }
}
