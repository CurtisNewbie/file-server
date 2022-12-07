package com.yongj.services;

import com.curtisnewbie.common.vo.*;
import com.yongj.vo.*;
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

    /**
     * Add file to folder
     *
     * @return number of files added
     */
    void addFileToVFolder(@Valid @NotNull AddFileToVFolderCmd cmd);

    /**
     * Remove file from folder
     */
    void removeFileFromVFolder(@Valid @NotNull RemoveFileFromVFolderCmd cmd);

    /**
     * Share folder
     */
    void shareVFolder(@Valid @NotNull ShareVFolderCmd cmd);

    /**
     * Remove granted access
     */
    void removeGrantedAccess(@Valid @NotNull RemoveGrantedVFolderAccessCmd cmd);

}
