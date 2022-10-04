package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.util.MapperUtils;
import com.curtisnewbie.common.util.Paginator;
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
import com.yongj.vo.ScanFsGroupResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageableList;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Service
public class FsGroupServiceImpl implements FsGroupService {

    @Autowired
    private FsGroupMapper fsGroupMapper;

    @Autowired
    private FsGroupConverter fsGroupConverter;

    private final RandomPicker<FsGroup> randomPicker = new ShuffleRandomPicker<>();

    @Override
    public FsGroup findFsGroupById(int id) {
        return fsGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public FsGroup findAnyFsGroupToWrite(FsGroupType type) {
        return randomPicker.pickRandom(fsGroupMapper.selectList(new LambdaQueryWrapper<FsGroup>()
                .eq(FsGroup::getMode, FsGroupMode.READ_WRITE.getValue())
                .eq(FsGroup::getType, type)
                .last("limit 100")));
    }

    @Override
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

    @Override
    public List<FsGroup> listFsGroups(Paginator.PagingParam p) {
        return fsGroupMapper.selectList(MapperUtils.limit(p.getOffset(), p.getLimit()));
    }

    @Override
    public void saveScanResult(ScanFsGroupResult result) {
        fsGroupMapper.update(null,
                MapperUtils.set(FsGroup::getScanTime, result.getScanTime())
                        .set(FsGroup::getSize, result.getSize())
                        .eq(FsGroup::getId, result.getId()));
    }
}
