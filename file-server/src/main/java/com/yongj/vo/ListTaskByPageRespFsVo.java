package com.yongj.vo;

import com.curtisnewbie.common.vo.PageableVo;
import lombok.Data;

import java.util.List;

/**
 * Response vo for listing tasks in pages
 *
 * @author yongjie.zhuang
 */
@Data
public class ListTaskByPageRespFsVo extends PageableVo {

    private List<TaskFsVo> list;
}