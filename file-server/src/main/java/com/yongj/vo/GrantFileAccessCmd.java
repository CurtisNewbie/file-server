package com.yongj.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Command object for granting file's access to other user
 *
 * @author yongjie.zhuang
 */
@Data
@Builder
public class GrantFileAccessCmd {

     /**
     * id of user who granted the access
     */
    private final int grantedByUserId;

    /**
     * id of the file
     */
    private final int fileId;

    /**
     * id of user who is given the access to the file
     */
    private final int grantedTo;

}
