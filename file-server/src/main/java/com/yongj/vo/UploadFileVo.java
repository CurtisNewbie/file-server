package com.yongj.vo;

import com.yongj.enums.FileUserGroupEnum;
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
public class UploadFileVo {

    /** uploader id */
    private int userId;

    /** uploader name */
    private String username;

    /** file name */
    @NotEmpty
    private String fileName;

    /** user group */
    @NotNull
    private FileUserGroupEnum userGroup;

    /** input stream */
    @NotNull
    private InputStream inputStream;
}
