package com.yongj.vo;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.lang.*;

import javax.validation.constraints.*;

/**
 * @author yongj.zhuang
 */
@Data
public class MakeDirReqVo {

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
    private Integer userGroup;
}
