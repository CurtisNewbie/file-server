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
public class ListFileInfoVo {

    private Iterable<FileInfoVo> fileInfoList;

    private PagingVo pagingVo;

}
