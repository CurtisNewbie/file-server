package com.yongj.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author yongj.zhuang
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PollFileEventReq extends EventSyncReq {
    private Long eventId;
    private Integer limit;
}
