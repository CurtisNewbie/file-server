package com.yongj.dao;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
public interface FileExtensionMapper {

    /**
     * Find all enabled file extensions
     */
    List<FileExtension> findAllEnabled();

    /**
     * Find names of all enabled file extensions
     */
    List<String> findNamesOfAllEnabled();

    /**
     * Select * of all extensions
     */
    List<FileExtension> findAll();

    /**
     * Update selective
     */
    void updateSelective(FileExtension fileExt);
}
