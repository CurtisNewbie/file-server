package com.yongj.io;

import com.yongj.exceptions.IllegalExtException;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Resolver of paths, which is also responsible for escape special characters, and concatenating relative paths.
 *
 * @author yongjie.zhuang
 */
@Validated
public interface PathResolver {

    String FILE_EXT_DELIMITER = ".";

    /**
     * Resolve absolute path
     *
     * @param uuid  file uuid
     * @param owner (uploadApp or uploaderId)
     * @return absolute path
     */
    String resolveAbsolutePath(@NotEmpty String uuid, @NotEmpty String owner, @NotEmpty String fsGroupFolder);

    /**
     * Resolve absolute path
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
     * @throws com.yongj.exceptions.IllegalExtException if file extension is invalid
     */
    void validateFileExtension(@NotEmpty String fileName);

    /**
     * Resolve absolute path for the folder
     *
     * @param folder folder name
     * @return absolute path
     */
    String resolveFolder(@NotEmpty String folder);

    /** Extract file extension */
    static String extractFileExt(String name) {
        Assert.notNull(name, "name is empty");
        name = name.trim();
        if (name.isEmpty() || name.endsWith(FILE_EXT_DELIMITER))
            throw new IllegalExtException("File name is empty or it ends with '.'");

        final int i = name.lastIndexOf('.');
        if (i == -1)
            throw new IllegalExtException("File extension not found");

        return name.substring(i + 1);
    }
}
