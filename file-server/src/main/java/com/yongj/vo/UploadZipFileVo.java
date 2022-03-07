package com.yongj.vo;

import com.yongj.enums.FileUserGroupEnum;
import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

/**
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UploadZipFileVo {

    /** uploader id */
    private int userId;

    /** uploader name */
    private String username;

    /** user group */
    private FileUserGroupEnum userGroup;

    /** zip file's name */
    private String zipFile;

    /** entries' name */
    private String[] entryNames;

    /** entries' inputstream */
    private InputStream[] inputStreams;
}
