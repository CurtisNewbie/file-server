package com.yongj.helper;

import java.util.Collections;
import java.util.List;

/**
 * Implementation that simply shuffles the list
 *
 * @author yongj.zhuang
 */
public class ShuffleRandomPicker<T> implements RandomPicker<T> {

    @Override
    public T pickRandom(List<T> tl) {
        if (tl == null || tl.isEmpty()) return null;
        if (tl.size() == 1) return tl.get(0);
        Collections.shuffle(tl);
        return tl.get(0);
    }
}
