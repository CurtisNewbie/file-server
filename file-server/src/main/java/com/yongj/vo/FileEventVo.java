package com.yongj.vo;

import com.curtisnewbie.common.dao.DaoSkeleton;
import com.yongj.enums.FEventType;
import lombok.Data;

/**
 * File Events
 *
 * @author yongj.zhuang
 */
@Data
public class FileEventVo extends DaoSkeleton {

    /** event type */
    private FEventType type;

    /** file key */
    private String fileKey;

}
