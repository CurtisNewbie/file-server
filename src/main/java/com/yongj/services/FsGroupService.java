package com.yongj.services;


import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.yongj.dao.FsGroup;
import com.yongj.enums.FsGroupMode;
import com.yongj.vo.FsGroupVo;
import com.yongj.vo.ListAllFsGroupReqVo;
import org.springframework.validation.annotation.Validated;

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
    FsGroup findFirstFsGroupForWrite();

    /**
     * Find list of fs_group in pages
     */
    PageablePayloadSingleton<List<FsGroupVo>> findByPage(@NotNull ListAllFsGroupReqVo param);

    /**
     * Update fs_group's mode
     */
    void updateFsGroupMode(int fsGroupId, @NotNull FsGroupMode mode) throws MsgEmbeddedException;
}
