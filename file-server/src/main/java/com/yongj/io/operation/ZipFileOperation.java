package com.yongj.io.operation;

import com.yongj.io.ZipCompressEntry;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Zip file operation
 *
 * @author yongjie.zhuang
 */
public interface ZipFileOperation {

    /**
     * Compress and write file into a single zip
     *
     * @param absPath absolute path of the file
     * @param entries entries (one or more)
     * @return size of file in bytes
     * @throws IOException
     */
    long compressFile(String absPath, List<ZipCompressEntry> entries) throws IOException;

    /**
     * Compress and write file into a single zip
     *
     * @param absPath absolute path of the file
     * @param entries files (one or more)
     * @return size of file in bytes
     * @throws IOException
     */
    long compressLocalFile(String absPath, List<File> entries) throws IOException;

}
