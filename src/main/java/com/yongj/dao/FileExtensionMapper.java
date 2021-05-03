package com.yongj.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Mapper
public interface FileExtensionMapper {

    @Select("SELECT * FROM file_extension WHERE is_enabled = 0")
    List<FileExtension> findAllEnabled();

    @Select("SELECT DISTINCT name FROM file_extension WHERE is_enabled = 0")
    List<String> findNamesOfAllEnabled();
}
