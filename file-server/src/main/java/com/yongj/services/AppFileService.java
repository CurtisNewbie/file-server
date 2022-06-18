package com.yongj.services;

import com.yongj.vo.UploadAppFileCmd;
import feign.Response;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * AppFile Service
 *
 * @author yongj.zhuang
 */
@Validated
public interface AppFileService {

    /**
     * Upload app file
     */
    String upload(@NotNull @Valid UploadAppFileCmd cmd) throws IOException;

    /**
     * Download app file
     */
    Response download(@NotEmpty String uuid) throws IOException;
}
