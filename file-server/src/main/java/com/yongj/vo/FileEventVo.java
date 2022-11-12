package com.yongj.vo;

import com.yongj.enums.FEventType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * File Events
 *
 * @author yongj.zhuang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEventVo {

    /** event id */
    private Integer eventId;

    /** event type */
    private FEventType type;

    /** file key */
    private String fileKey;

}
