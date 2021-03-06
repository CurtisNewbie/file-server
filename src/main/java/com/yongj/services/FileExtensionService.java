package com.yongj.services;

import com.github.pagehelper.PageInfo;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
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

    /**
     * Find name of all enabled file extensions
     */
    List<String> getNamesOfAllEnabled();

    /**
     * Find details of all file extensions
     */
    List<FileExtVo> getDetailsOfAll();

    /**
     * Selectively find details of all file extensions (with pagination)
     */
    PageInfo<FileExtVo> getDetailsOfAllByPageSelective(@NotNull ListFileExtReqVo param);

    /**
     * Selectively Update file extension by id
     */
    void updateFileExtSelective(@NotNull FileExtVo fileExtVo);

}
