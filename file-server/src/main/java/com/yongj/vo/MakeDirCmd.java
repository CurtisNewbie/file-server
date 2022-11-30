package com.yongj.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.yongj.enums.FUserGroup;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author yongj.zhuang
 */
@Data
public class MakeDirCmd {

    /** Key of parent file */
    @Nullable
    private String parentFile;

    /** name of the directory */
    @NotEmpty
    private String name;

    /** uploader id, i.e., user.id */
    @JsonIgnore
    @NotNull
    private Integer uploaderId;

    /** userNo of uploader */
    @JsonIgnore
    @NotNull
    private String userNo;

    /** uploader name */
    @JsonIgnore
    @NotEmpty
    private String uploaderName;

    /** User Group */
    @Nullable
    private FUserGroup userGroup;
}
