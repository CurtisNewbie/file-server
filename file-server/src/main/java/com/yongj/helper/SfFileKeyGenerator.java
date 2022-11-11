package com.yongj.helper;

import com.curtisnewbie.common.util.IdUtils;
import org.springframework.stereotype.Component;

/**
 * @author yongj.zhuang
 */
@Component
public class SfFileKeyGenerator implements FileKeyGenerator {

    @Override
    public String generate() {
        /*
            ZZZ is for backward compatibility, previously UUID was used.
            using 'ZZZ' may be helpful in the sense that the key may be added
            at the bottom of the index tree, so that no tree re-balancing is needed
         */
        return IdUtils.gen("ZZZ");
    }
}
