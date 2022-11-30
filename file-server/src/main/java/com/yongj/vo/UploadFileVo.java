package com.yongj.vo;

import com.yongj.enums.FUserGroup;
import lombok.Builder;
import lombok.Data;
import org.springframework.lang.*;

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

    /** userNo of uploader */
    private String userNo;

    /** uploader name */
    private String username;

    /** file name */
    @NotEmpty
    private String fileName;

    /** user group */
    @NotNull
    private FUserGroup userGroup;

    /** input stream */
    @NotNull
    private InputStream inputStream;

    /** Key of parent file */
    @Nullable
    private String parentFile;
}
