package com.yongj.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectBasicFileInfoParam {

    /** id of user */
    private Integer userId;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** name of the file */
    private String filename;

    /** id of uploader, if this is set, only the files with this uploaderId is queried */
    private Integer uploaderId;

}
