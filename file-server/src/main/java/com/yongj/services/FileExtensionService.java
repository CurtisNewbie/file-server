package com.yongj.services;

import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.yongj.dao.FileExtension;
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
    PageablePayloadSingleton<List<FileExtVo>> getDetailsOfAllByPageSelective(@NotNull ListFileExtReqVo param);

    /**
     * Selectively Update file extension by id
     */
    void updateFileExtSelective(@NotNull FileExtVo fileExtVo);

    /**
     * Add new file extension
     *
     * @throws com.yongj.exceptions.IllegalExtException   if the name of the file extension is illegal
     */
    void addFileExt(@NotNull FileExtension fileExtension);
}
