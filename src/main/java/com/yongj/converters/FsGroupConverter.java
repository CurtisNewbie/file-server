package com.yongj.converters;

import com.yongj.dao.FsGroup;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author yongjie.zhuang
 */
@Mapper
public interface FsGroupConverter {

    FsGroupConverter converter = Mappers.getMapper(FsGroupConverter.class);

    FsGroup toDo(FsGroupVo v);

    FsGroup toDo(ListAllFsGroupReqVo v);
}
