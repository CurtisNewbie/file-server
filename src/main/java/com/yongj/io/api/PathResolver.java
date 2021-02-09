package com.yongj.io.api;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Resolver of paths, which is also responsible for escape special characters, and concatenating relative paths.
 *
 * @author yongjie.zhuang
 */
@Validated
public interface PathResolver {

    /**
     * Resolve the given relative path, and may delete or escape special characters if found
     *
     * @param relPath
     * @return absolute path
     * @throws com.yongj.exceptions.IllegalPathException if the path contains illegal character, such as ".."
     */
    String resolvePath(@NotEmpty String relPath);

    /**
     * Validate the file extension of the given path
     *
     * @param relPath
     * @return TRUE if is valid, else FALSE
     */
    boolean validateFileExtension(@NotEmpty String relPath);
}
