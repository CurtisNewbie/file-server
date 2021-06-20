package com.yongj.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines how the file writing operation is undertaken
 * <p>
 * To specify or provide your own implementation, see {@link IOOperationConfig}
 * </p>
 *
 * @author yongjie.zhuang
 */
public interface WriteFileOperation {


    /**
     * Write file
     *
     * @param absPath     absolute path of the file
     * @param inputStream inputStream
     * @return size of file in bytes
     * @throws IOException
     */
    long writeFile(String absPath, InputStream inputStream) throws IOException;

}
