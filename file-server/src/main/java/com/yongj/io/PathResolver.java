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
     * Resolve absolute path to a file in fsGroup
     *
     * @param uuid  file uuid
     * @param owner (uploadApp or uploaderId)
     * @return absolute path
     */
    String resolveAbsolutePath(@NotEmpty String uuid, @NotEmpty String owner, @NotEmpty String fsGroupFolder);

    /**
     * Resolve absolute path to a file in fsGroup
     *
     * @param uuid       file uuid
     * @param uploaderId uploaderId
     * @return absolute path
     */
    default String resolveAbsolutePath(@NotEmpty String uuid, int uploaderId, @NotEmpty String fsGroupFolder) {
        return resolveAbsolutePath(uuid, String.valueOf(uploaderId), fsGroupFolder);
    }

    /**
     * Validate the file extension of the given path
     *
     * @param fileName file's name
     */
    void validateFileExtension(@NotEmpty String fileName);

    /**
     * Resolve absolute path to the special base folder, this isn't used for uploading/saving files, this is used for
     * all sorts of temp stuff used by the app
     *
     * @param folder folder name
     * @return absolute path
     */
    String resolveBaseFolder(@NotEmpty String folder);

}
