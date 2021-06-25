package com.yongj.vo;

import com.curtisnewbie.common.vo.PagingVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongjie.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListAccessLogInfoRespVo {

    private Iterable<AccessLogInfoFsVo> accessLogInfoList;

    private PagingVo pagingVo;

}
