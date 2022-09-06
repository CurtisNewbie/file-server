package com.yongj.helper;

import com.yongj.enums.*;

/**
 * FileType Resolver
 *
 * @author yongj.zhuang
 */
public interface FileTypeResolver {

    FileType resolve(String uuid);

    default boolean isFile(String uuid) {
        return resolve(uuid) == FileType.FILE;
    }

}
