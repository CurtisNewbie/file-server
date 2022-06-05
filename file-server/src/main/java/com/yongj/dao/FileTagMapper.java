package com.yongj.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.util.EnhancedMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for file_tag
 *
 * @author yongjie.zhuang
 */
public interface FileTagMapper extends EnhancedMapper<FileTag> {

    List<String> listFileTags(@Param("userId") int userId);

    IPage<Tag> listTagsForFile(Page p, @Param("userId") int userId, @Param("fileId") int fileId);
}
