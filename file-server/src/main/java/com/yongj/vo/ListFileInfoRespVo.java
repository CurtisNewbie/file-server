package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListFileInfoRespVo extends PageableVo {

    private Iterable<FileInfoWebVo> fileInfoList;

}
