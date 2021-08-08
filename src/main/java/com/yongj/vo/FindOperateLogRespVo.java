package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response vo for finding operate log
 *
 * @author yongjie.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindOperateLogRespVo {

    private List<OperateLogFsVo> operateLogVoList;

    private PagingVo pagingVo;

}
