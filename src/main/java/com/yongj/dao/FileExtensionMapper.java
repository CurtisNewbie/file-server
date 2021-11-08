package com.yongj.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    IPage<FileExtension> findAllSelective(Page p, @Param("p") FileExtension param);

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
