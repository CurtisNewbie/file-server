package com.yongj.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yongj.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yongj.zhuang
 */
public interface VFolderMapper extends BaseMapper<VFolder> {

    VFolderWithOwnership findVFolderWithOwnership(@Param("userNo") String userNo, @Param("folderNo") String folderNo);

    VFolder findVFolderForUser(@Param("userNo") String userNo, @Param("folderNo") String folderNo);

    Integer findIdForFolderWithName(@Param("userNo") String userNo, @Param("name") String name);

    Page<VFolderListResp> listVFolders(Page page, @Param("r") ListVFolderReq req);

    Page<FileInfoVo> listFilesInVFolders(Page forPage, @Param("r") ListVFolderFilesReq req);

    List<VFolderBrief> listOwnedVFolderBrief(@Param("userNo") String userNo);
}
