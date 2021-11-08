package com.yongj.converters;

import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.ListTaskByPageRespVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.yongj.vo.ListTaskByPageReqFsVo;
import com.yongj.vo.TaskFsVo;
import org.mapstruct.Mapper;

/**
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface TaskFsConverter {

    TaskFsVo toFsVo(TaskVo tv);

    TaskFsVo toFsVo(ListTaskByPageRespVo tv);

    ListTaskByPageReqVo toListTaskByPageReqFsVo(ListTaskByPageReqFsVo rq);

}
