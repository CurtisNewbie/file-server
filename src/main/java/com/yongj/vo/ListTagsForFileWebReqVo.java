package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author yongjie.zhuang
 */
@Data
public class ListTagsForFileWebReqVo extends PageableVo {

    @NotNull
    private Integer fileId;
}
