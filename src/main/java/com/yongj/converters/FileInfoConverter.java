package com.yongj.converters;

import com.yongj.dao.FileInfo;
import com.yongj.vo.FileInfoVo;
import org.mapstruct.Mapper;

/**
 * Converter for FileInfo
 *
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface FileInfoConverter {

    FileInfoVo toVo(FileInfo fi);
}
