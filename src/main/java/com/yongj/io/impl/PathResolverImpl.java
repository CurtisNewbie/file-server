package com.yongj.io.impl;

import com.yongj.exceptions.IllegalPathException;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


/**
 * @author yongjie.zhuang
 */
@Component
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);
    private static final String FILE_EXT_DELIMITER = ".";

    /**
     * unmodifiable, initialised set of supported file extension, use this instead of {@link #supportedExt} in any
     * operation
     */
    private Set<String> supportedFileExtension;

    @Value("${base.path}")
    private String basePath;

    @Value("${supported.file.extension}")
    private List<String> supportedExt;

    @PostConstruct
    void init() {
        logger.info("[INIT] PathResolver using base path: '{}'", basePath);
        Set<String> tempSet = new TreeSet<>();
        supportedExt.forEach(ext -> {
            final String trimmedExt = ext.trim();
            if (StringUtils.hasText(trimmedExt))
                tempSet.add(trimmedExt);
        });
        if (tempSet.isEmpty())
            throw new IllegalStateException("${supported.file.extension} is empty");
        supportedFileExtension = Collections.unmodifiableSet(tempSet);
    }

    @Override
    public String resolvePath(String relPath) {
        if (relPath.contains(".."))
            throw new IllegalPathException("Path contains '..', which is illegal");
        return relPath.startsWith(File.separator) ? basePath + relPath : basePath + File.separator + relPath;
    }

    @Override
    public boolean validateFileExtension(String relPath) {
        relPath = relPath.trim();
        if (relPath.isEmpty() || relPath.endsWith(FILE_EXT_DELIMITER))
            return false;
        StringBuilder parsedExt = new StringBuilder();
        for (int i = relPath.length() - 1; i >= 0; i--) {
            if (relPath.charAt(i) == '.') {
                break;
            } else if (relPath.charAt(i) == ' ') {
                return false;
            } else {
                parsedExt.insert(0, relPath.charAt(i));
            }
        }
        if (parsedExt.length() == 0)
            return false;

        return supportedFileExtension.contains(parsedExt.toString());
    }
}
