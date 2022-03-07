package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Data
public class ListGrantedAccessRespVo extends PageableVo {

    private List<FileSharingWebVo> list;
}
