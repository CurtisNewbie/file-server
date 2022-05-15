package com.yongj.vo;

import com.yongj.enums.FileUserGroupEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

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

    /** file name */
    private String fileName;

    /** user group */
    private FileUserGroupEnum userGroup;

    /** input stream */
    private InputStream inputStream;
}
