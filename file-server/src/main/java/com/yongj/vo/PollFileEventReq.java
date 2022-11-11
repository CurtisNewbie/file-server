package com.yongj.vo;

import lombok.Data;

/**
 * @author yongj.zhuang
 */
@Data
public class PollFileEventReq {

    private String secret;
    private Long eventId;
    private Integer limit;
}
