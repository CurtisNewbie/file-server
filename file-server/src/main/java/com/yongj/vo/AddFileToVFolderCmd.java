package com.yongj.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.*;

/**
 * @author yongj.zhuang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddFileToVFolderCmd {

    @NotEmpty
    private String userNo;

    @NotEmpty
    private String folderNo;

    /** file key (uuid) */
    @NotEmpty
    private List<String> fileKeys;
}
