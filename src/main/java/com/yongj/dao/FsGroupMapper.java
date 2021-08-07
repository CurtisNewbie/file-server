package com.yongj.dao;

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
}