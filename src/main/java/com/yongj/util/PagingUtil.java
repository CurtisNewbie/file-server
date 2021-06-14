package com.yongj.util;

import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * Utility class for Paging
 *
 * @author yongjie.zhuang
 */
public final class PagingUtil {

    private PagingUtil() {
    }

    /**
     * Convenient method for creating PageInfo wherein the total number of elements is manually specified
     */
    public static <T> PageInfo<T> pageInfoOf(List<T> list, long total) {
        PageInfo<T> p = PageInfo.of(list);
        p.setTotal(total);
        return p;
    }
}
