package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
public interface FileExtensionMapper extends BaseMapper<FileExtension> {

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
     * Select * selectively
     */
    IPage<FileExtension> findAllSelective(Page p, @Param("p") FileExtension param);

    /**
     * Select first id of file extension with the given name
     *
     * @param name name
     * @return id
     */
    Integer findIdByName(@Param("name") String name);

    Integer getIdOfEnabledFileExt(@Param("ext") String fileExt);
}
