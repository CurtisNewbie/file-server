package com.yongj.util;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.util.AssertUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Utility class for path
 *
 * @author yongjie.zhuang
 */
public final class PathUtils {

    /** file extension delimiter */
    public static final String FILE_EXT_DELIMITER = ".";

    /** pattern to escape special chars in filename */
    public static final Pattern escapePat = Pattern.compile("[ ()（）\\[\\]【】]");

    private PathUtils() {
    }

    /**
     * Extract file name (short name) from the path
     *
     * @throws IllegalArgumentException if the path is null or empty
     */
    public static String extractFileName(String path) {
        if (!StringUtils.hasLength(path))
            throw new UnrecoverableException("Path doesn't have length, cannot extract filename");
        int i = path.lastIndexOf(File.separator);
        if (i == -1)
            return path;
        if (i == path.length() - 1)
            throw new UnrecoverableException("Path is illegal, it should not end with path separator");
        return path.substring(i + 1);
    }

    /**
     * Get next filename, this method is used to avoid filename collision
     * <p>
     * This method requires providing the one returned by previous method call when collision occurs again and again, so
     * that this method can simply increases the suffix number.
     * <p>
     * E.g., for multiple with the same name 'file.txt'. Multiple calls to this method may return file_1.txt,
     * file_2.txt, file_3.txt
     */
    public static String getNextFilename(String name) {
        // file suffix
        final String suffix = extractFileExt(name);

        // to avoid name collision, if we found a file with the same name, we add suffix to it
        final String beforeSuffix = name.substring(0, name.lastIndexOf(FILE_EXT_DELIMITER));
        final char last = beforeSuffix.charAt(beforeSuffix.length() - 1);

        // the first one being file_1.txt, the next being file_2.txt
        // the 'pre' is 'file_1.' and the 'suffix' is 'txt'
        String pre = beforeSuffix + "_1.";
        if (last >= '0' && last <= '9') {
            final int j = beforeSuffix.lastIndexOf("_");
            if (j > -1) { // won't happen if there is no bug :D
                int num = Integer.parseInt(beforeSuffix.substring(j + 1)) + 1;

                // e.g., if beforeSuffix is 'file_1'
                // then we have:
                // j -> 4
                // beforeSuffix.substring(0, j+1) -> 'file_'
                pre = beforeSuffix.substring(0, j + 1) + num + ".";
            }
        }

        // 'pre' is the part before file suffix including '.', 'suffix' is just the file suffix without '.'
        return pre + suffix;
    }

    /** Escape filename, replace special characters with '_' */
    public static String escapeFilename(String name) {
        AssertUtils.notNull(name, "name is empty");
        return escapePat.matcher(name).replaceAll("_").replaceAll("_+", "_");
    }

    /** Extract file extension */
    public static String extractFileExt(String name) {
        AssertUtils.notNull(name, "name is empty");
        name = name.trim();
        if (name.isEmpty() || name.endsWith(FILE_EXT_DELIMITER))
            throw new UnrecoverableException("File name is empty or it ends with '.'");

        final int i = name.lastIndexOf('.');
        if (i == -1)
            throw new UnrecoverableException("File extension not found");

        return name.substring(i + 1);
    }
}
