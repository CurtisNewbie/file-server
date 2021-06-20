package com.yongj.io;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration of IO Operations
 *
 * @author yongjie.zhuang
 */
@Configuration
public class IOOperationConfig {

    @Bean
    public DeleteFileOperation deleteFileOperation() {
        return new DefaultDeleteFileOperation();
    }
}
