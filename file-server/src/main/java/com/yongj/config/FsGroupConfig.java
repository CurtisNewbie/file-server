package com.yongj.config;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * Config for FsGroup
 *
 * @author yongj.zhuang
 */
@Configuration
public class FsGroupConfig {

    private static final Pattern whiteSpacePat = Pattern.compile("[ \t\n\u00a0　]");

    @Value("${fs-group.base.prefix:}")
    private String baseFolderPrefix;

    public String sanitize(String baseFolder) {
        baseFolder = baseFolder.trim();

        if (baseFolder.contains(".."))
            throw new UnrecoverableException("Your are not allowed to use '..' in base folder path");

        if (whiteSpacePat.matcher(baseFolder).matches())
            throw new UnrecoverableException("Base folder path should not include special, whitespace characters");

        if (StringUtils.hasText(baseFolderPrefix) && !baseFolder.startsWith(baseFolderPrefix))
            throw new UnrecoverableException(String.format("Base folder path must start with '%s'", baseFolderPrefix));

        return baseFolder;
    }
}
