package com.yongj.util;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Utilities to copy bean's properties
 *
 * @author yongjie.zhuang
 */
public final class BeanCopyUtils {

    private BeanCopyUtils() {
    }

    /**
     * Copy properties, and convert to the given type
     *
     * @param source     source object
     * @param targetType targetType
     * @param <T>        target's generic type
     * @param <V>        source's generic type
     * @return targetObject (that is created using default constructor)
     */
    public static <T, V> V toType(T source, Class<V> targetType) {
        Objects.requireNonNull(targetType);
        if (source == null) {
            return null;
        }
        V v;
        try {
            v = targetType.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to convert bean", e);
        }
        BeanUtils.copyProperties(source, v);
        return v;
    }

    /**
     * Copy properties, and convert to the given type
     *
     * @param srcList    source object list
     * @param targetType targetType
     * @param <T>        target's generic type
     * @param <V>        source's generic type
     * @return targetObject list (wherein each object is created using default constructor)
     */
    public static <T, V> List<V> toTypeList(List<T> srcList, Class<V> targetType) {
        Objects.requireNonNull(targetType);
        if (srcList == null) {
            return new ArrayList<>();
        }
        return srcList.stream().map(t -> {
            return toType(t, targetType);
        }).collect(Collectors.toList());
    }

    /**
     * Copy properties, and convert to the given type
     *
     * @param srcPageInfo source object page info
     * @param targetType  targetType
     * @param <T>         target's generic type
     * @param <V>         source's generic type
     * @return targetObject pageinfo (wherein each object is created using default constructor)
     */
    public static <T, V> PageInfo<V> toPageList(PageInfo<T> srcPageInfo, Class<V> targetType) {
        Objects.requireNonNull(targetType);
        if (srcPageInfo == null) {
            return new PageInfo<>();
        }
        PageInfo<V> vPage = PageInfo.of(toTypeList(srcPageInfo.getList(), targetType));
        vPage.setTotal(srcPageInfo.getTotal());
        return vPage;
    }

}
