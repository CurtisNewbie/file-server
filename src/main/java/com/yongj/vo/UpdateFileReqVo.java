package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.yongj.enums.FileUserGroupEnum;
import lombok.Data;

/**
 * Request vo for updating file's user_group
 *
 * @author yongjie.zhuang
 */
@Data
public class UpdateFileReqVo {

    /** file's id */
    private Integer id;

    /** user_group */
    private Integer userGroup;

    /** fileName */
    private String name;


    public void validate() throws MsgEmbeddedException {
        ValidUtils.requireNonNull(id, "id can't be null");
        ValidUtils.assertTrue(userGroup != null || name != null, "Illegal Arguments, must have values to update");
        FileUserGroupEnum fug = EnumUtils.parse(userGroup, FileUserGroupEnum.class);
        ValidUtils.requireNonNull(fug, "Illegal UserGroup value");
    }
}
