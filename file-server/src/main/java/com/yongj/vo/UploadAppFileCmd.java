package com.yongj.vo;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import java.io.InputStream;

/**
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UploadAppFileCmd {

    /** file name */
    @NotEmpty
    private String fileName;

    /** upload app */
    @NotEmpty
    private String uploadApp;

    /** user id */
    @Nullable
    private String userId;

    /** input stream */
    private InputStream inputStream;
}
