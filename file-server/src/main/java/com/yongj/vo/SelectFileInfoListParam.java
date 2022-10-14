package com.yongj.vo;

import com.yongj.enums.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectFileInfoListParam {

    /** id of user */
    private Integer userId;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** name of the file */
    private String filename;

    /** only return files that is uploaded by current user */
    private boolean filterOwnedFiles;

    /** tag name */
    private String tagName;

    /** File type */
    private FileType fileType;

    /** Parent file uuid */
    private String parentFile;

    /** userNo */
    private String userNo;
}
