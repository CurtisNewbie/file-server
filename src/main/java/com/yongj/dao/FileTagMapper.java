package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for file_tag
 *
 * @author yongjie.zhuang
 */
public interface FileTagMapper extends BaseMapper<FileTag> {

    List<String> listFileTags(@Param("userId") int userId);

    List<String> listTagsForFile(@Param("userId") int userId, @Param("fileId") int fileId);
}
