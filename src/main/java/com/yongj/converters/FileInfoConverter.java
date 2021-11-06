package com.yongj.converters;

import com.yongj.dao.FileInfo;
import com.yongj.vo.FileInfoVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Converter for FileInfo
 *
 * @author yongjie.zhuang
 */
@Mapper
public interface FileInfoConverter {

    FileInfoConverter converter = Mappers.getMapper(FileInfoConverter.class);

    FileInfoVo toVo(FileInfo fi);
}
