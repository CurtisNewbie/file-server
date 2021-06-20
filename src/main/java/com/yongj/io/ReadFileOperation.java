package com.yongj.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Defines how the file reading operation is undertaken
 * <p>
 * To specify or provide your own implementation, see {@link IOOperationConfig}
 * </p>
 *
 * @author yongjie.zhuang
 */
public interface ReadFileOperation {


    /**
     * Read file
     *
     * @param absPath      absolute path of the file
     * @param outputStream outputStream
     * @throws IOException
     */
    void readFile(String absPath, OutputStream outputStream) throws IOException;

}
