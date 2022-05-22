package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

/**
 * Request vo for listing all fsGroups
 *
 * @author yongjie.zhuang
 */
@Data
public class ListAllFsGroupReqVo extends PageableVo {

    private FsGroupVo fsGroup;

}
