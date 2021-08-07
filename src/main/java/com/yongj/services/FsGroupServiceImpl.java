package com.yongj.services;

import com.yongj.dao.FsGroup;
import com.yongj.dao.FsGroupMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yongjie.zhuang
 */
@DubboService
@Transactional
@Service
public class FsGroupServiceImpl implements FsGroupService{

    @Autowired
    private FsGroupMapper fsGroupMapper;

    @Override
    public FsGroup findFsGroupById(int id) {
        return fsGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public FsGroup findFirstFsGroupForWrite() {
        return fsGroupMapper.findFirstForWrite();
    }
}
