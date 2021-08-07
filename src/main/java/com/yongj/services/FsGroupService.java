package com.yongj.services;


import com.yongj.dao.FsGroup;
import org.springframework.validation.annotation.Validated;

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
}
