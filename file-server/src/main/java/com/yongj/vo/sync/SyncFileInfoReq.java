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
public class SyncFileInfoReq extends EventSyncReq {
    private String fileKey;
}
