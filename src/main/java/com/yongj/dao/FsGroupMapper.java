package com.yongj.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper for fs_group
 *
 * @author yongjie.zhuang
 */
public interface FsGroupMapper {

    int insert(FsGroup record);

    FsGroup selectByPrimaryKey(Integer id);

    List<FsGroup> selectAll();

    int updateByPrimaryKey(FsGroup record);

    FsGroup findFirstForWrite();

    List<FsGroup> findByPage(FsGroup param);

    void updateFsGroupModeById(@Param("id") int fsGroupId, @Param("mode") int mode);
}