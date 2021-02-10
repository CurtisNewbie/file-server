package com.yongj.util;

import com.yongj.exceptions.IllegalPathException;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * Utility class for path
 *
 * @author yongjie.zhuang
 */
public final class PathUtils {

    private PathUtils() {
    }

    /**
     * Extract file name (short name) from the path
     *
     * @throws IllegalArgumentException if the path is null or empty
     */
    public static String extractFileName(String path) {
        if (!StringUtils.hasLength(path))
            throw new IllegalPathException("Path doesn't have length, cannot extract filename");
        int i = path.lastIndexOf(File.separator);
        if (i == -1)
            return path;
        if (i == path.length() - 1)
            throw new IllegalPathException("Path is illegal, it should not end with path separator");
        return path.substring(i + 1);
    }
}
