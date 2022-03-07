package com.yongj.converters;

import com.curtisnewbie.module.task.vo.ListTaskHistoryByPageRespVo;
import com.yongj.vo.TaskHistoryWebVo;
import org.mapstruct.Mapper;

/**
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface TaskHistoryFsConverter {

    TaskHistoryWebVo toWebVo(ListTaskHistoryByPageRespVo rs);
}
