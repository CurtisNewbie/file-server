package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListFileExtRespVo {

    private List<FileExtVo> fileExtList;

    /** paging param */
    private PagingVo pagingVo;
}
