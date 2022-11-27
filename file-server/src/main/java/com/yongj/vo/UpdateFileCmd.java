package com.yongj.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yongj.enums.FUserGroup;
import lombok.Builder;
import lombok.Data;

/**
 * Command object to update file's info
 *
 * @author yongjie.zhuang
 */
@Data
@Builder
public class UpdateFileCmd {

    /** file's id */
    private int id;

    /** file's name */
    private String fileName;

    /** file's user group */
    private FUserGroup userGroup;

    /** id of user who updated the file */
    @JsonIgnore
    private int updatedById;
}
