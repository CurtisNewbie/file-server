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
     * Find Vfolder
     */
    VFolderDomain findVFolder(@NotEmpty String userNo, @NotEmpty String folderNo);

    /**
     * Create new Vfolder for current user
     *
     * @return folderNo
     */
    String createVFolder(@NotEmpty String userNo, @NotEmpty String name);

}
