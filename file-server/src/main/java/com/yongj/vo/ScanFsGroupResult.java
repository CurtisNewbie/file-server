package com.yongj.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author yongj.zhuang
 */
@Builder
@Data
public class ScanFsGroupResult {
    private final int id;
    private final LocalDateTime scanTime;
    private final long size;
}
