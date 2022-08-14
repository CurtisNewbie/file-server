package com.yongj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongj.zhuang
 */
@Data
@Configuration
@ConfigurationProperties("file-service")
public class FileServiceConfig {

    private int maxZipEntries = Integer.MAX_VALUE;
}
