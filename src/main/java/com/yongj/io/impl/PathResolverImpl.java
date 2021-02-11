package com.yongj.io.impl;

import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


/**
 * @author yongjie.zhuang
 */
@Component
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);
    private static final String FILE_EXT_DELIMITER = ".";

    /**
     * unmodifiable, initialised set of supported file extension, use this instead of {@link #configSupportedExt} in any
     * operation
     */
    private Set<String> supportedFileExtension;

    /** base path, or the base directory for this file-server */
    @Value("${base.path}")
    private String BASE_PATH;

    /** URI of {@link #BASE_PATH} */
    private URI BASE_PATH_URI;

    @Value("${supported.file.extension}")
    private List<String> configSupportedExt;

    @PostConstruct
    void init() {
        logger.info("[INIT] PathResolver using base path: '{}'", BASE_PATH);
        Set<String> tempSet = new TreeSet<>();
        configSupportedExt.forEach(ext -> {
            final String trimmedExt = ext.trim();
            if (StringUtils.hasText(trimmedExt))
                tempSet.add(trimmedExt);
        });
        if (tempSet.isEmpty())
            throw new IllegalStateException("${supported.file.extension} is empty");
        supportedFileExtension = Collections.unmodifiableSet(tempSet);
        BASE_PATH_URI = Paths.get(BASE_PATH).toUri();
    }

    @Override
    public String resolvePath(String relPath) {
        if (relPath.contains(".."))
            throw new IllegalPathException("Path contains '..', which is illegal");
        return relPath.startsWith(File.separator) ? BASE_PATH + relPath : BASE_PATH + File.separator + relPath;
    }

    @Override
    public void validateFileExtension(String relPath) {
        relPath = relPath.trim();
        if (relPath.isEmpty() || relPath.endsWith(FILE_EXT_DELIMITER))
            throw new IllegalExtException("Path is empty or it contains '..' illegal sequence of chars");
        StringBuilder parsedExt = new StringBuilder();
        for (int i = relPath.length() - 1; i >= 0; i--) {
            if (relPath.charAt(i) == '.') {
                break;
            } else if (relPath.charAt(i) == ' ') {
                throw new IllegalExtException("File extension shouldn't contain space");
            } else {
                parsedExt.insert(0, relPath.charAt(i));
            }
        }
        if (parsedExt.length() == 0)
            throw new IllegalExtException("File extension not found");

        if (!supportedFileExtension.contains(parsedExt.toString())) {
            throw new IllegalExtException(String.format("File extension '%s' not supported", parsedExt));
        }
    }

    @Override
    public String getBaseDir() {
        return BASE_PATH;
    }

    @Override
    public List<String> relativizePaths(Stream<Path> pathStream) {
        List<String> relPaths = new ArrayList<>();
        pathStream.forEach(path -> {
            relPaths.add(relativizePath(path));
        });
        return relPaths;
    }

    @Override
    public String relativizePath(Path absPath) {
        return BASE_PATH_URI.relativize(absPath.toUri()).getPath();
    }

    @Override
    public String relativizePath(String absPath) {
        Path path = Paths.get(absPath);
        return relativizePath(path);
    }

    @Override
    public List<String> getSupportedFileExtension() {
        List<String> list = new ArrayList<>(supportedFileExtension.size());
        supportedFileExtension.forEach(e -> list.add(e));
        return list;
    }
}
