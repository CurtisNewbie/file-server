package com.yongj.services;

import com.yongj.vo.CreateVFolderCmd;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * VFolder Service
 *
 * @author yongj.zhuang
 */
@Validated
public interface VFolderService {


    /**
     * Create VFolder for current user
     */
    @NotEmpty
    String createVFolder(@Valid @NotNull CreateVFolderCmd cmd);
}
