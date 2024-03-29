package com.yongj.vo;

import com.curtisnewbie.common.vo.*;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

/**
 * @author yongj.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListVFolderReq extends PageableVo<Void> {

    @JsonIgnore
    private String userNo;

    private String name;
}
