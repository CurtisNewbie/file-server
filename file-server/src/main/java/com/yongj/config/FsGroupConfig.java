package com.yongj.config;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Config for FsGroup
 *
 * @author yongj.zhuang
 */
@Configuration
public class FsGroupConfig {

    @Value("${fs-group.base.prefix:}")
    private String baseFolderPrefix;

    public String sanitize(String baseFolder) {
        baseFolder = baseFolder.trim();

        if (baseFolder.contains(".."))
            throw new UnrecoverableException("Your are not allowed to use '..' in base folder path");

        if (StringUtils.hasText(baseFolderPrefix) && !baseFolder.startsWith(baseFolderPrefix))
            throw new UnrecoverableException(String.format("Base folder path must start with '%s'", baseFolderPrefix));

        return baseFolder;
    }
}
