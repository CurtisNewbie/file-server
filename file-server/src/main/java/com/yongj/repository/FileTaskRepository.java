package com.yongj.repository;

import com.yongj.domain.FileTaskDomain;
import com.yongj.enums.FileTaskType;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author yongj.zhuang
 */
@Validated
public interface FileTaskRepository {

    FileTaskDomain createFileTask(@NotEmpty String userNo, @NotNull FileTaskType type, @Nullable String description);

    @Nullable
    FileTaskDomain findByTaskNo(String taskNo);

    @Nullable
    String lookupTaskNoByKey(String fileKey);

}
