package com.yongj.vo;

import com.curtisnewbie.common.vo.*;
import lombok.*;

/**
 * @author yongj.zhuang
 */
@Data
public class ListGrantedFolderAccessReq extends PageableVo<Void> {

    private String folderNo;
}
