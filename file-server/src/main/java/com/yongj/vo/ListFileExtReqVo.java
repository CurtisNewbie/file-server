package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import com.yongj.enums.FExtIsEnabled;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class ListFileExtReqVo extends PageableVo {

    /**
     * name of file extension, e.g., "txt"
     */
    private String name;

    /**
     * whether this file extension is enabled, 0-enabled, 1-disabled
     */
    private FExtIsEnabled isEnabled;
}
