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
public class UploadFileVo {

    /** uploader id */
    private int userId;
    /** uploader name */
    private String username;
    private String fileName;
    private FileUserGroupEnum userGroup;
    private InputStream inputStream;
}
