package com.yongj.vo;

import lombok.Builder;
import lombok.Data;

import java.nio.channels.FileChannel;

/**
 * @author yongj.zhuang
 */
@Builder
@Data
public class AppFileDownloadInfo {

    /** name of the file */
    private String name;

    /** size of file in bytes */
    private long size;

    private FileChannel fileChannel;
}
