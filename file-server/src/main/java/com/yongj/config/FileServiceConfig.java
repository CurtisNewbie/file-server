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

    /** uploading speed in mb/s */
    private int uploadSpeedLimit = -1;

    /** export compression speed in mb/s */
    private int compressSpeedLimit = -1;

    /** compression level (0-9) */
    private int compressLevel = 0;
}
