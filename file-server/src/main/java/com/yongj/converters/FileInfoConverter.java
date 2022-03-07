package com.yongj.converters;

import com.yongj.dao.FileInfo;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.FileInfoWebVo;
import com.yongj.vo.PhysicDeleteFileVo;
import org.mapstruct.Mapper;

/**
 * Converter for FileInfo
 *
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface FileInfoConverter {

    FileInfoVo toVo(FileInfo fi);

    FileInfoWebVo toWebVo(FileInfoVo fi);

    PhysicDeleteFileVo toPhysicDeleteFileVo(FileInfo fi);
}
