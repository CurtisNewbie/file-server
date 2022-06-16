package com.yongj.helper;

import java.util.List;

/**
 * Random Picker
 *
 * @author yongj.zhuang
 */
@FunctionalInterface
public interface RandomPicker<T> {

    /**
     * Pick random one from the list, if the list is empty, it may simply return null
     */
    T pickRandom(List<T> tl);

}
