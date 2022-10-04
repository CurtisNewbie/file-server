package com.yongj.services;


import com.curtisnewbie.common.util.Paginator;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.FsGroup;
import com.yongj.enums.FsGroupMode;
import com.yongj.enums.FsGroupType;
import com.yongj.vo.AddFsGroupReq;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import com.yongj.vo.ScanFsGroupResult;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Service for fs_group
 *
 * @author yongjie.zhuang
 */
@Validated
public interface FsGroupService {

    /**
     * Find fs_group by id
     *
     * @param id id
     */
    FsGroup findFsGroupById(int id);

    /**
     * Find first fs_group for writing (uploading files)
     */
    FsGroup findAnyFsGroupToWrite(@NotNull FsGroupType type);

    /**
     * Find list of fs_group in pages
     */
    PageableList<FsGroupVo> findByPage(@NotNull ListAllFsGroupReqVo param);

    /**
     * Update fs_group's mode
     */
    void updateFsGroupMode(int fsGroupId, @NotNull FsGroupMode mode, @Nullable String updatedBy);

    /**
     * List fsGroups
     */
    List<FsGroup> listFsGroups(@NotNull Paginator.PagingParam p);

    /**
     * Save scan result
     */
    void saveScanResult(@NotNull ScanFsGroupResult result);

    /**
     * Add FsGroup
     */
    void addFsGroup(@NotNull @Valid AddFsGroupReq req);

}
