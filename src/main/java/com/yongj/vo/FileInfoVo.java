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
    private String name;

    /**
     * size in bytes
     */
    private Long sizeInBytes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSizeInBytes() {
        return sizeInBytes;
    }

    public void setSizeInBytes(Long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
