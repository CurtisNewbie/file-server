package com.yongj.vo.sync;

import lombok.Data;
import lombok.ToString;

/**
 * @author yongj.zhuang
 */
@Data
public class EventSyncReq {

    private String secret;

    @ToString.Include(name = "secret")
    public String maskSecret() {
        return "****";
    }
}
