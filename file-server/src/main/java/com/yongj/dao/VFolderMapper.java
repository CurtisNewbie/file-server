package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author yongj.zhuang
 */
public interface VFolderMapper extends BaseMapper<VFolder> {

    VFolder findVFolderForUser(@Param("userNo") String userNo, @Param("folderNo") String folderNo);

    Integer findIdForFolderWithName(@Param("userNo") String userNo, @Param("name") String name);
}
