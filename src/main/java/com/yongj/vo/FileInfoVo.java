package com.yongj.vo;

/**
 * @author yongjie.zhuang
 */
public class FileInfoVo {

    /**
     * UUID
     */
    private String uuid;

    /**
     * fileName
     */
    private String fileName;

    /**
     * size in bytes
     */
    private long sizeInBytes;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
