package com.yongj.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of IO Operations
 *
 * @author yongjie.zhuang
 */
@Configuration
public class IOOperationConfig {

    private static final Logger logger = LoggerFactory.getLogger(IOOperationConfig.class);
    private static final String TEMP_FOLDER_DELETE_FILE_OPERATION_TYPE = "temp-folder";
    private static final String PHYSICALLY_DELETE_FILE_OPERATION_TYPE = "file-delete";

    @Value("${file.operation.delete-type:" + TEMP_FOLDER_DELETE_FILE_OPERATION_TYPE + "}")
    private String deleteFileOperationType;

    @Bean
    public DeleteFileOperation deleteFileOperation() {
        DeleteFileOperation impl;
        if (deleteFileOperationType.equals(TEMP_FOLDER_DELETE_FILE_OPERATION_TYPE)) {
            impl = new TempFolderDeleteFileOperation();
        } else if (deleteFileOperationType.equals(PHYSICALLY_DELETE_FILE_OPERATION_TYPE)) {
            impl = new PhysicallyDeleteFileOperation();
        } else {
            impl = new TempFolderDeleteFileOperation();
        }
        logger.info("File deleting operation will use implementation: {}", impl.getClass().getName());
        return impl;
    }

    @Bean
    public ReadFileOperation readFileOperation() {
        ReadFileOperation impl = new ChannelReadFileOperation();
        logger.info("File reading operation will use implementation: {}", impl.getClass().getName());
        return impl;
    }

    @Bean
    public WriteFileOperation writeFileOperation() {
        WriteFileOperation impl = new ChannelWriteFileOperation();
        logger.info("File writing operation will use implementation: {}", impl.getClass().getName());
        return impl;
    }
}
