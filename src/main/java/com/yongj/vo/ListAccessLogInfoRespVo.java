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
public class ListAccessLogInfoRespVo extends PageableVo {

    private Iterable<AccessLogInfoFsVo> accessLogInfoList;

}
