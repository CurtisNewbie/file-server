package com.yongj.helper;

import com.yongj.dao.FsGroup;
import com.yongj.enums.FsGroupType;
import org.springframework.lang.Nullable;

/**
 * Supplier of FsGroup for write op
 *
 * @author yongj.zhuang
 */
public interface WriteFsGroupSupplier {

    /**
     * Supplier of a writable FsGroup for the given type
     */
    @Nullable
    FsGroup supply(FsGroupType fsGroupType);
}
