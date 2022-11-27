package com.yongj.vo;

import com.fasterxml.jackson.annotation.*;
import com.yongj.enums.FUserGroup;
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

    /** User Group */
    @Nullable
    private FUserGroup userGroup;
}
