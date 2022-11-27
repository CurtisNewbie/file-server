package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.config.FsGroupConfig;
import com.yongj.converters.FsGroupConverter;
import com.yongj.dao.FsGroup;
import com.yongj.dao.FsGroupMapper;
import com.yongj.enums.FsGroupMode;
import com.yongj.enums.FsGroupType;
import com.yongj.helper.RandomPicker;
import com.yongj.helper.ShuffleRandomPicker;
import com.yongj.vo.AddFsGroupReq;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import com.yongj.vo.ScanFsGroupResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.locks.Lock;

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
    @Autowired
    private RedisController redisController;
    @Autowired
    private FsGroupConfig fsGroupConfig;

    private final RandomPicker<FsGroup> randomPicker = new ShuffleRandomPicker<>();

    @Override
    public FsGroup findFsGroupById(int id) {
        return fsGroupMapper.selectByPrimaryKey(id);
    }

    @Override
    public FsGroup findAnyFsGroupToWrite(FsGroupType type) {
        return fsGroupMapper.pickRandom(FsGroupMode.READ_WRITE, type);
    }

    @Override
    public PageableList<FsGroupVo> findByPage(@NotNull ListAllFsGroupReqVo param) {
        return toPageableList(
                fsGroupMapper.findByPage(param.page(), fsGroupConverter.toDo(param.getFsGroup())),
                fsGroupConverter::toVo
        );
    }

    @Override
    public void updateFsGroupMode(int fsGroupId, @NotNull FsGroupMode mode, @Nullable String updatedBy) {
        final LambdaQueryWrapper<FsGroup> condition = new LambdaQueryWrapper<FsGroup>()
                .eq(FsGroup::getId, fsGroupId)
                .eq(FsGroup::getIsDel, IsDel.NORMAL.getValue());

        final FsGroup param = new FsGroup();
        param.setMode(mode);
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

    @Override
    public void addFsGroup(AddFsGroupReq req) {
        log.info("adding fsGroup, req: {}", req);

        final Lock lock = redisController.getLock("fs:fsgroup:add");
        LockUtils.lockAndRun(lock, () -> {

            // try to validate and sanitize the baseFolder
            final String baseFolder = fsGroupConfig.sanitize(req.getBaseFolder());

            // check if we already have a fsGroup with the same base folder
            final FsGroup ex = fsGroupMapper.selectOne(
                    MapperUtils.select(FsGroup::getId)
                            .eq(FsGroup::getBaseFolder, baseFolder)
                            .last("limit 1"));
            AssertUtils.isNull(ex, "There is already a FsGroup with same base folder");

            // we check whether we can create the base folder
            try {
                final Path path = Paths.get(baseFolder);
                Files.createDirectory(path);
            } catch (Exception e) {
                log.warn("Failed to create directories", e);
                if (e instanceof InvalidPathException)
                    throw new UnrecoverableException(String.format("'%s' is invalid", baseFolder));
                if (e instanceof FileAlreadyExistsException)
                    throw new UnrecoverableException(String.format("'%s' is a file", baseFolder));
                throw new UnrecoverableException("Failed to create fsGroup, please try again later");
            }

            FsGroup fsGroup = BeanCopyUtils.toType(req, FsGroup.class);
            fsGroup.setMode(FsGroupMode.READ);
            fsGroupMapper.insert(fsGroup);
        });
    }
}
