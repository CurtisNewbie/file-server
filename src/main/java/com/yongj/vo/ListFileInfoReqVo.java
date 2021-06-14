package com.yongj.vo;

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

    /** paging param */
    private PagingVo pagingVo;

}
