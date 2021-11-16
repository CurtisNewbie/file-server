package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.ValidUtils;
import lombok.Data;

/**
 * @author yongjie.zhuang
 */
@Data
public class GrantAccessToUserReqVo {

    /** file's id */
    private Integer fileId;

    /** name of user who is granted access to the file */
    private String grantedTo;

    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(fileId);
        ValidUtils.requireNonNull(grantedTo);
    }

}
