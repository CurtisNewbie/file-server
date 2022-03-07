package com.yongj.vo;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;

/**
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UploadAppFileCmd {

    /** file name */
    private String fileName;

    /** upload app */
    private String uploadApp;

    /** input stream */
    private InputStream inputStream;
}
