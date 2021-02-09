package com.yongj.io.impl;

import com.yongj.exceptions.IllegalPathException;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;


/**
 * @author yongjie.zhuang
 */
@Service
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);

    @Value("${base.path}")
    private String basePath;

    @PostConstruct
    void init() {
        logger.info("[INIT] PathResolver using base path: '{}'", basePath);
    }

    @Override
    public String resolvePath(String relPath) {
        if (relPath.contains(".."))
            throw new IllegalPathException("Path contains '..', which is illegal");
        return relPath.startsWith(File.separator) ? basePath + relPath : basePath + File.separator + relPath;
    }
}
