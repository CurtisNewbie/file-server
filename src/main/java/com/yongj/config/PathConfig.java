package com.yongj.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yongjie.zhuang
 */
@Component
public final class PathConfig {

    private static final Logger logger = LoggerFactory.getLogger(PathConfig.class);

    /** base path, or the base directory for this file-server */
    private final String basePath;

    /** URI of {@link #basePath} */
    private final URI basePathUri;

    public PathConfig(@Value("${base.path}") String basePath) throws IOException {
        this.basePath = basePath;
        logger.info("[INIT] Using base path: '{}'", this.basePath);
        Path bp = Paths.get(this.basePath);
        Files.createDirectories(bp);
        basePathUri = bp.toUri();
    }

    public String getBasePath() {
        return basePath;
    }

    public URI getBasePathUri() {
        return basePathUri;
    }
}
