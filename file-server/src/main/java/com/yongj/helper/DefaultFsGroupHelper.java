package com.yongj.helper;

import com.yongj.dao.FsGroup;
import com.yongj.enums.FsGroupType;
import com.yongj.services.FsGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * @author yongj.zhuang
 */
@Component
public class DefaultFsGroupHelper implements FsGroupIdResolver, WriteFsGroupSupplier {

    @Autowired
    private FsGroupService fsGroupService;

    @Override
    public FsGroup resolve(int id) {
        return fsGroupService.findFsGroupById(id);
    }

    @Override
    public FsGroup supply(FsGroupType fsGroupType) {
        Assert.notNull(fsGroupType, "FsGroupType == null");
        return fsGroupService.findAnyFsGroupToWrite(fsGroupType);
    }
}
