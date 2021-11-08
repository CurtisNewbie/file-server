package com.yongj.services;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.PagingUtil;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.yongj.converters.FsGroupConverter;
import com.yongj.dao.FsGroup;
import com.yongj.dao.FsGroupMapper;
import com.yongj.enums.FsGroupMode;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Transactional
@Service
public class FsGroupServiceImpl implements FsGroupService {

    @Autowired
    private FsGroupMapper fsGroupMapper;

    @Autowired
    private FsGroupConverter fsGroupConverter;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FsGroup findFsGroupById(int id) {
        return fsGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FsGroup findFirstFsGroupForWrite() {
        return fsGroupMapper.findFirstForWrite();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<FsGroupVo>> findByPage(@NotNull ListAllFsGroupReqVo param) {
        return PagingUtil.toPageList(
                fsGroupMapper.findByPage(PagingUtil.forPage(param.getPagingVo()), fsGroupConverter.toDo(param)),
                fsGroupConverter::toVo
        );
    }

    @Override
    public void updateFsGroupMode(int fsGroupId, @NotNull FsGroupMode mode) throws MsgEmbeddedException {
        FsGroup fsg = fsGroupMapper.selectByPrimaryKey(fsGroupId);
        ValidUtils.requireNonNull(fsg, "fs_group not exists");
        fsGroupMapper.updateFsGroupModeById(fsGroupId, mode.getValue());
    }
}
