package com.yongj.converters;

import com.yongj.dao.FsGroup;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import org.mapstruct.Mapper;

/**
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface FsGroupConverter {

    FsGroup toDo(FsGroupVo v);

    FsGroup toDo(ListAllFsGroupReqVo v);
}
