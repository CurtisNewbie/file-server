package com.yongj.io.impl;

import com.yongj.config.PathConfig;
import com.yongj.dao.FileExtensionMapper;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.exceptions.IllegalPathException;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * @author yongjie.zhuang
 */
@Component
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);
    private static final String FILE_EXT_DELIMITER = ".";
    private static final Pattern INVALID_CHAR_PATTERN = Pattern.compile("^.*[\\&\\|\\*:\\?\"\\<\\>\\t].*$");

    @Autowired
    private FileExtensionMapper fileExtensionMapper;
    @Autowired
    private PathConfig pathConfig;

    @Override
    public String resolvePath(String relPath) {
        if (relPath.contains(".."))
            throw new IllegalPathException("Path contains '..', which is illegal");
        String absPath = relPath.startsWith(File.separator) ?
                pathConfig.getBasePath() + relPath :
                pathConfig.getBasePath() + File.separator + relPath;
        logger.debug("Resolving path of '{}', resolved absolute path: '{}'", relPath, absPath);
        return absPath;
    }

    @Override
    public String validatePath(String relPath) {
        if (relPath.isEmpty() || relPath.matches("\\.[a-zA-Z]+"))
            throw new IllegalPathException("Path doesn't include filename");
        if (INVALID_CHAR_PATTERN.matcher(relPath).matches())
            throw new IllegalPathException("Path contains illegal characters");
        relPath = relPath.replaceAll("\\s", "-");
        return relPath;
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

        Set<String> fileExtSet = new HashSet<>(fileExtensionMapper.findNamesOfAllEnabled());
        if (!fileExtSet.contains(parsedExt.toString())) {
            throw new IllegalExtException(String.format("File extension '%s' not supported", parsedExt));
        }
    }
}
