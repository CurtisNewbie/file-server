package com.yongj.vo;

import com.yongj.enums.*;
import lombok.*;

import java.time.*;

/**
 * @author yongj.zhuang
 */
@Data
public class GrantedFolderAccess {
    private String userNo;
    private String username; // TODO
    private LocalDateTime createTime;
}
