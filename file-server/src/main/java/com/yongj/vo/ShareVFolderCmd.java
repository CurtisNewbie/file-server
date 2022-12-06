package com.yongj.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author yongj.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShareVFolderCmd {

    @NotEmpty
    private String currUserNo;

    @NotEmpty
    private String sharedToUserNo;

    @NotEmpty
    private String folderNo;
}
