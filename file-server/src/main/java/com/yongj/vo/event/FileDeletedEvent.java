package com.yongj.vo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author yongj.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDeletedEvent {

    private String fileKey;
}
