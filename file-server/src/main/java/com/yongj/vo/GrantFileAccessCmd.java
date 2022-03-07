package com.yongj.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Command object for granting file's access to other user
 *
 * @author yongjie.zhuang
 */
@Data
public class GrantFileAccessCmd {

    /**
     * Id of user who granted the access
     */
    private final String grantedBy;

    /**
     * id of the file
     */
    private final int fileId;

    /**
     * id of user who is given the access to the file
     */
    private final int grantedTo;

    @Builder
    public GrantFileAccessCmd(String grantedBy, int fileId, int grantedTo) {
        this.grantedBy = grantedBy;
        this.fileId = fileId;
        this.grantedTo = grantedTo;
    }
}
