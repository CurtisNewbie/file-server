package com.yongj.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yongj.enums.FsGroupType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author yongj.zhuang
 */
@Data
public class AddFsGroupReq {

    /** group name */
    @TableField("name")
    @NotEmpty
    private String name;

    /** base folder */
    @TableField("base_folder")
    @NotEmpty
    private String baseFolder;

    /** Type of a fs_group */
    @TableField("type")
    @NotNull
    private FsGroupType type;
}
