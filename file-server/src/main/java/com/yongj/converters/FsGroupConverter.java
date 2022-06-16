package com.yongj.converters;

import com.yongj.dao.FsGroup;
import com.yongj.vo.FsGroupVo;
import org.mapstruct.Mapper;

/**
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface FsGroupConverter {

    FsGroup toDo(FsGroupVo v);

    FsGroupVo toVo(FsGroup f);
}
