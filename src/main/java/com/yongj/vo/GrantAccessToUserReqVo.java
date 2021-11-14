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

    /** id of user who is granted access to the file */
    private Integer grantedTo;

    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(fileId);
        ValidUtils.requireNonNull(grantedTo);
    }

}
