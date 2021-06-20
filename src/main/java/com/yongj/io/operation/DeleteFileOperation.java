package com.yongj.io.operation;

import com.yongj.io.IOOperationConfig;

import java.io.IOException;

/**
 * Defines how the file deleting operation is undertaken
 * <p>
 * To specify or provide your own implementation, see {@link IOOperationConfig}
 * </p>
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
