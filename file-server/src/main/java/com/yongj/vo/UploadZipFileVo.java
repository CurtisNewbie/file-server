package com.yongj.vo;

import com.yongj.enums.FileUserGroupEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UploadZipFileVo {

    /** uploader id */
    private int userId;

    /** userNo of uploader */
    private String userNo;

    /** uploader name */
    private String username;

    /** user group */
    private FileUserGroupEnum userGroup;

    /** zip file's name */
    private String zipFile;

    private MultipartFile[] multipartFiles;
}
