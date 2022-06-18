package com.yongj.vo;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.InputStream;

/**
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UploadAppFileCmd {

    /** owner's id (nullable) */
    @Nullable
    private Integer userId;

    /** file name */
    @NotEmpty
    private String fileName;

    /** input stream */
    @NotNull
    private InputStream inputStream;

    /** app name */
    @NotEmpty
    private String appName;

}
