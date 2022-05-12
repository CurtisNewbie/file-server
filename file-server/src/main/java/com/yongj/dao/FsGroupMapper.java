package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for fs_group
 *
 * @author yongjie.zhuang
 */
public interface FsGroupMapper extends BaseMapper<FsGroup> {

    FsGroup selectByPrimaryKey(Integer id);

    List<FsGroup> selectAll();

    FsGroup findFirstForWrite();

    IPage<FsGroup> findByPage(Page p, @Param("p") FsGroup param);

    void updateFsGroupModeById(@Param("id") int fsGroupId, @Param("mode") int mode);
}