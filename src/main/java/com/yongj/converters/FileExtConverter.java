package com.yongj.converters;

import com.yongj.dao.FileExtension;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Converter for FileExtension
 *
 * @author yongjie.zhuang
 */
@Mapper
public interface FileExtConverter {

    FileExtConverter converter = Mappers.getMapper(FileExtConverter.class);

    FileExtVo toVo(FileExtension t);

    FileExtension toDo(ListFileExtReqVo l);

    FileExtension toDo(FileExtVo v);

}
