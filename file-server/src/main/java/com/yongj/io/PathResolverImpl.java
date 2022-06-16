package com.yongj.io;

import com.yongj.config.PathConfig;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.services.FileExtensionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;
import java.io.File;


/**
 * @author yongjie.zhuang
 */
@Component
public class PathResolverImpl implements PathResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathResolverImpl.class);
    private static final String FILE_EXT_DELIMITER = ".";

    @Autowired
    private FileExtensionService fileExtensionService;
    @Autowired
    private PathConfig pathConfig;

    @Override
    public String resolveAbsolutePath(@NotEmpty String uuid, String owner, String fsGroupFolder) {
        return fsGroupFolder + File.separator +
                owner + File.separator + uuid;
    }

    @Override
    public void validateFileExtension(String name) {
        logger.info("Validating file extension: {}", name);
        name = name.trim();
        if (name.isEmpty() || name.endsWith(FILE_EXT_DELIMITER))
            throw new IllegalExtException("File name is empty or it ends with '.'");

        final int i = name.lastIndexOf('.');
        if (i == -1)
            throw new IllegalExtException("File extension not found");

        final String fe = name.substring(i + 1);
        if (!fileExtensionService.isEnabled(fe)) {
            throw new IllegalExtException(String.format("File extension '%s' is not allowed", fe));
        }
    }

    @Override
    public String resolveFolder(@NotEmpty String folder) {
        String absPath = pathConfig.getBasePath() + File.separator
                + folder;
        logger.debug("Resolving path for folder: '{}', resolved absolute path: '{}'",
                folder, absPath);
        return absPath;
    }
}
