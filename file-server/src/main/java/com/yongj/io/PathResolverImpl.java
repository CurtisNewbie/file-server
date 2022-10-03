package com.yongj.io;

import com.curtisnewbie.common.util.AssertUtils;
import com.yongj.config.PathConfig;
import com.yongj.services.FileExtensionService;
import com.yongj.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;


/**
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class PathResolverImpl implements PathResolver {

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
        log.info("Validating file extension: {}", name);
        final String fe = PathUtils.extractFileExt(name);
        AssertUtils.isTrue(fileExtensionService.isEnabled(fe), "File extension '%s' is not allowed", fe);
    }

    @Override
    public String resolveFolder(String folder) {
        String absPath = pathConfig.getBasePath() + File.separator + folder;
        log.debug("Resolving path for folder: '{}', resolved absolute path: '{}'", folder, absPath);
        return absPath;
    }
}
