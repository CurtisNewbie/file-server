package com.yongj.repository;

import com.yongj.domain.VFolderDomain;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Repository for VFolderDomain
 *
 * @author yongj.zhuang
 */
@Validated
public interface VFolderRepository {

    /**
     * Build Vfolder
     */
    VFolderDomain buildVFolder(@NotEmpty String userNo, @NotEmpty String folderNo);

    /**
     * Build new Vfolder, if one exists for current user, exception is thrown
     */
    VFolderDomain buildForNewVFolder(@NotEmpty String userNo, @NotEmpty String name);

}
