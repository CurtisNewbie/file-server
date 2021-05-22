package com.yongj.dto;

/**
 * @author yongjie.zhuang
 */
public class FileInfo {

    /**
     * fileName
     */
    private String fileName;

    /**
     * size in bytes
     */
    private long sizeInBytes;

    public FileInfo(String fileName, Long sizeInBytes) {
        this.fileName = fileName;
        if (sizeInBytes == null)
            this.sizeInBytes = 0;
        else
            this.sizeInBytes = sizeInBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }
}
