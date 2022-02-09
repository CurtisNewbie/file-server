package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.yongj.converters.FsGroupConverter;
import com.yongj.dao.FsGroup;
import com.yongj.dao.FsGroupMapper;
import com.yongj.enums.FsGroupMode;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageList;

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
        return toPageList(
                fsGroupMapper.findByPage(forPage(param.getPagingVo()), fsGroupConverter.toDo(param)),
                fsGroupConverter::toVo
        );
    }

    @Override
    public void updateFsGroupMode(int fsGroupId, @NotNull FsGroupMode mode, @Nullable String updatedBy) {
        final QueryWrapper<FsGroup> condition = new QueryWrapper<FsGroup>()
                .eq("id", fsGroupId)
                .eq("is_del", IsDel.NORMAL.getValue());

        final FsGroup param = new FsGroup();
        param.setMode(mode.getValue());
        param.setUpdateTime(LocalDateTime.now());
        param.setUpdateBy(updatedBy);

        fsGroupMapper.update(param, condition);
    }
}
