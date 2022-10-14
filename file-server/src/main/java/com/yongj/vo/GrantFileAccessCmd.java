package com.yongj.vo;

import com.curtisnewbie.common.trace.TUser;
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
     * user who granted the access
     */
    private final TUser grantedBy;

    /**
     * id of the file
     */
    private final int fileId;

    /**
     * id of user who is given the access to the file
     */
    private final int grantedTo;

}
