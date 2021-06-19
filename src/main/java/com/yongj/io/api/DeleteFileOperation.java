package com.yongj.io.api;

import java.io.IOException;

/**
 * Defines how the file deleting operation is committed
 *
 * @author yongjie.zhuang
 */
public interface DeleteFileOperation {

    /**
     * Delete file
     *
     * @param absPath absolute path
     */
    void deleteFile(String absPath) throws IOException;
}
