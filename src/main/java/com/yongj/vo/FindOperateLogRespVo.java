package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.util.List;

/**
 * Response vo for finding operate log
 *
 * @author yongjie.zhuang
 */
@Data
public class FindOperateLogRespVo extends PageableVo {

    private List<OperateLogFsVo> operateLogVoList;
}
