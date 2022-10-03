package com.yongj.io;

import com.yongj.config.PathConfig;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.services.FileExtensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author yongjie.zhuang
 */
@Component
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);

    @Autowired
    private FileExtensionService fileExtensionService;
    @Autowired
    private PathConfig pathConfig;

    @Override
    public String resolveAbsolutePath(String uuid, String owner, String fsGroupFolder) {
        return fsGroupFolder + File.separator +
                owner + File.separator + uuid;
    }

    @Override
    public void validateFileExtension(String name) {
        logger.info("Validating file extension: {}", name);
        final String fe = PathResolver.extractFileExt(name);
        if (!fileExtensionService.isEnabled(fe)) {
            throw new IllegalExtException(String.format("File extension '%s' is not allowed", fe));
        }
    }

    @Override
    public String resolveFolder(String folder) {
        String absPath = pathConfig.getBasePath() + File.separator
                + folder;
        logger.debug("Resolving path for folder: '{}', resolved absolute path: '{}'",
                folder, absPath);
        return absPath;
    }
}
