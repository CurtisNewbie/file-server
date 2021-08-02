package com.yongj.dao;

import org.apache.ibatis.annotations.Param;

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

    /**
     * Select * selectively
     */
    List<FileExtension> findAllSelective(FileExtension param);

    /**
     * Insert a new file extension
     */
    void insert(FileExtension ext);

    /**
     * Select first id of file extension with the given name
     *
     * @param name name
     * @return id
     */
    Integer findIdByName(@Param("name") String name);
}
