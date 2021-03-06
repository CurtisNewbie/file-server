package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListFileInfoReqVo {

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** name of the file */
    private String filename;

    /** ownership of file, 0-all files, 1-files that belong to current users */
    private Integer ownership;

    /** id of the user */
    @JsonIgnore
    private Integer userId;

    /** paging param */
    private PagingVo pagingVo;

}
