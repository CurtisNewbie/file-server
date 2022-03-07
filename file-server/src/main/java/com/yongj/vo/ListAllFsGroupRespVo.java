package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response vo for listing all fsGroups
 *
 * @author yongjie.zhuang
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ListAllFsGroupRespVo extends PageableVo {

    private List<FsGroupVo> fsGroups;

}
