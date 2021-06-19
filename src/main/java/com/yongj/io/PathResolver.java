package com.yongj.io;

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
     * Resolve absolute path for the given uuid and userId
     *
     * @param uuid
     * @param userId
     * @return absolute path
     */
    String resolveAbsolutePath(@NotEmpty String uuid, int userId);

    /**
     * Validate the file extension of the given path
     *
     * @param relPath
     * @throws com.yongj.exceptions.IllegalExtException if file extension is invalid
     */
    void validateFileExtension(@NotEmpty String relPath);
}
