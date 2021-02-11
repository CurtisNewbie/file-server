package com.yongj.io.api;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

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
    String escapePath(@NotEmpty String relPath);

    /**
     * Validate the file extension of the given path
     *
     * @param relPath
     * @throws com.yongj.exceptions.IllegalExtException if file extension is invalid
     */
    void validateFileExtension(@NotEmpty String relPath);

    /**
     * Get base dir
     *
     * @return path
     */
    String getBaseDir();

    /**
     * Extract relative path from the base path
     *
     * @param absPath
     * @return
     */
    List<String> relativizePaths(Stream<Path> absPath);

    /**
     * Extract relative path from the base path
     *
     * @param absPath
     * @return
     */
    String relativizePath(Path absPath);

    /**
     * Extract relative path from the base path
     *
     * @param absPath
     * @return
     */
    String relativizePath(String absPath);

    /**
     * Get the supported file extensions in a list
     */
    List<String> getSupportedFileExtension();
}
