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
     * <p>
     * This method calls {@link #resolveAbsolutePath(String, String, String)}
     * </p>
     *
     * @param uuid
     * @param userId
     * @param fsGroupFolder
     * @return absolute path
     */
    String resolveAbsolutePath(@NotEmpty String uuid, int userId, String fsGroupFolder);

    /**
     * Resolve absolute path for the given uuid and owner
     *
     * @param uuid
     * @param owner         (e.g., uploadApp or uploaderId)
     * @param fsGroupFolder
     * @return absolute path
     */
    String resolveAbsolutePath(@NotEmpty String uuid, String owner, String fsGroupFolder);

    /**
     * Validate the file extension of the given path
     *
     * @param relPath
     * @throws com.yongj.exceptions.IllegalExtException if file extension is invalid
     */
    void validateFileExtension(@NotEmpty String relPath);

    /**
     * Resolve absolute path for the folder
     *
     * @param folder folder name
     * @return absolute path
     */
    String resolveFolder(@NotEmpty String folder);
}
