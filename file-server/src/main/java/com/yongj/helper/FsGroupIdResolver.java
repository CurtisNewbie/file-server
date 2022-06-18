package com.yongj.helper;

import com.yongj.dao.FsGroup;
import org.springframework.lang.Nullable;

/**
 * Resolver of FsGroup by id
 *
 * @author yongj.zhuang
 */
public interface FsGroupIdResolver {

    /**
     * Resolve FsGroup by id
     *
     * @return FsGroup (nullable)
     */
    @Nullable
    FsGroup resolve(int id);
}
