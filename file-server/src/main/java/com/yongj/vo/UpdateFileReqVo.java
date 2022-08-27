package com.yongj.vo;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.*;
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


    public void validate() {
        AssertUtils.notNull(id, "id can't be null");
        AssertUtils.isTrue(userGroup != null || name != null, "Illegal Arguments, must have values to update");
        FileUserGroupEnum fug = EnumUtils.parse(userGroup, FileUserGroupEnum.class);
        AssertUtils.notNull(fug, "Illegal UserGroup value");
    }
}
