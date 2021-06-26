package com.yongj.services;

import com.yongj.vo.FileExtVo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Service for file extensions
 *
 * @author yongjie.zhuang
 */
@Validated
public interface FileExtensionService {

    List<String> getNamesOfAllEnabled();

    List<FileExtVo> getDetailsOfAll();

    void updateFileExtSelective(@NotNull FileExtVo fileExtVo);

}
