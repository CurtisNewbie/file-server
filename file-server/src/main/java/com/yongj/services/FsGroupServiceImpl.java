package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.converters.FsGroupConverter;
import com.yongj.dao.FsGroup;
import com.yongj.dao.FsGroupMapper;
import com.yongj.enums.FsGroupMode;
import com.yongj.enums.FsGroupType;
import com.yongj.helper.RandomPicker;
import com.yongj.helper.ShuffleRandomPicker;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;

import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageableList;

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

    private final RandomPicker<FsGroup> randomPicker = new ShuffleRandomPicker<>();

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FsGroup findFsGroupById(int id) {
        return fsGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public FsGroup findAnyFsGroupToWrite(FsGroupType type) {
        return randomPicker.pickRandom(fsGroupMapper.selectList(new LambdaQueryWrapper<FsGroup>()
                .eq(FsGroup::getMode, FsGroupMode.READ_WRITE)
                .last("limit 100")));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageableList<FsGroupVo> findByPage(@NotNull ListAllFsGroupReqVo param) {
        return toPageableList(
                fsGroupMapper.findByPage(forPage(param.getPagingVo()), fsGroupConverter.toDo(param.getFsGroup())),
                fsGroupConverter::toVo
        );
    }

    @Override
    public void updateFsGroupMode(int fsGroupId, @NotNull FsGroupMode mode, @Nullable String updatedBy) {
        final LambdaQueryWrapper<FsGroup> condition = new LambdaQueryWrapper<FsGroup>()
                .eq(FsGroup::getId, fsGroupId)
                .eq(FsGroup::getIsDel, IsDel.NORMAL.getValue());

        final FsGroup param = new FsGroup();
        param.setMode(mode.getValue());
        fsGroupMapper.update(param, condition);
    }
}
