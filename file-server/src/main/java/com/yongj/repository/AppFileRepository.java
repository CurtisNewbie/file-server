package com.yongj.repository;

import com.yongj.domain.AppFileDomain;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * Repository for AppFile
 *
 * @author yongj.zhuang
 */
@Validated
public interface AppFileRepository {

    /**
     * Build for AppFile with the uuid
     */
    AppFileDomain buildForUuid(@NotEmpty String uuid);

    /**
     * Build empty AppFile Domain
     */
    AppFileDomain buildEmpty();
}
