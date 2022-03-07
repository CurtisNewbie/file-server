package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
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
public class ListFileExtRespVo extends PageableVo {

    private List<FileExtVo> fileExtList;

}
