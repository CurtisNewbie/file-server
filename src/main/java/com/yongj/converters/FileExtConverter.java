package com.yongj.converters;

import com.yongj.dao.FileExtension;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
import org.mapstruct.Mapper;

/**
 * Converter for FileExtension
 *
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface FileExtConverter {

    FileExtVo toVo(FileExtension t);

    FileExtension toDo(ListFileExtReqVo l);

    FileExtension toDo(FileExtVo v);

}
