package com.yongj.services;

import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.FileExtension;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
import com.yongj.vo.UpdateFileExtReq;
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
     * Selectively find details of all file extensions (with pagination)
     */
    PageableList<FileExtVo> getDetailsOfAllByPageSelective(@NotNull ListFileExtReqVo param);

    /**
     * Selectively Update file extension by id
     */
    void updateFileExtension(@NotNull UpdateFileExtReq req);

    /**
     * Add new file extension
     *
     * @throws IllegalExtException if the name of the file extension is illegal
     */
    void addFileExt(@NotNull FileExtension fileExtension);

    /**
     * Check whether the file extension is enabled
     */
    boolean isEnabled(String fileExt);
}
