package com.yongj.io.operation;

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines how the file writing operation is undertaken
 *
 * @author yongjie.zhuang
 */
public interface WriteFileOperation {


    /**
     * Write file
     *
     * @param absPath     absolute path of the file
     * @param inputStream inputStream
     * @return size of the file
     */
    long writeFile(String absPath, InputStream inputStream) throws IOException;

}
