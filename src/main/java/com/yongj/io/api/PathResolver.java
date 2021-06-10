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
     * Resolve the given relative path
     *
     * @param relPath
     * @return absolute path
     * @throws com.yongj.exceptions.IllegalPathException if the path contains illegal character, such as ".."
     */
    String resolvePath(@NotEmpty String relPath);

    /**
     * Delete or escape special characters if necessary
     *
     * @param relPath
     * @return escaped path
     * @throws com.yongj.exceptions.IllegalPathException if the path only contains illegal character
     */
    String validatePath(@NotEmpty String relPath);

    /**
     * Validate the file extension of the given path
     *
     * @param relPath
     * @throws com.yongj.exceptions.IllegalExtException if file extension is invalid
     */
    void validateFileExtension(@NotEmpty String relPath);
}
