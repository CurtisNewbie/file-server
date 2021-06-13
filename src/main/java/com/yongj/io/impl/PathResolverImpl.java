package com.yongj.io.impl;

import com.yongj.config.PathConfig;
import com.yongj.dao.FileExtensionMapper;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.io.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author yongjie.zhuang
 */
@Component
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);
    private static final String FILE_EXT_DELIMITER = ".";

    @Autowired
    private FileExtensionMapper fileExtensionMapper;
    @Autowired
    private PathConfig pathConfig;

    @Override
    public String resolveAbsolutePath(@NotEmpty String uuid, int userId) {
        String absPath = pathConfig.getBasePath() + File.separator
                + userId + File.separator
                + uuid;
        logger.debug("Resolving path for UUID: '{}' and userId: '{}' resolved absolute path: '{}'",
                uuid, userId, absPath);
        return absPath;
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
        Set<String> fileExtSet = fileExtensionMapper
                .findNamesOfAllEnabled()
                .stream()
                .map(e -> e.toLowerCase())
                .collect(Collectors.toSet());
        final String extToBeValidated = parsedExt.toString().trim().toLowerCase();
        if (!fileExtSet.contains(extToBeValidated)) {
            throw new IllegalExtException(String.format("File extension '%s' not supported", parsedExt));
        }
    }
}
