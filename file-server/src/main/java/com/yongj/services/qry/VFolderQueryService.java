package com.yongj.services.qry;

import com.curtisnewbie.common.vo.PageableList;
import com.yongj.vo.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Query Service for VFolder
 *
 * @author yongj.zhuang
 */
@Validated
public interface VFolderQueryService {

    /**
     * List vfolders
     */
    PageableList<VFolderListResp> listVFolders(@NotNull ListVFolderReq req);

    /**
     * List files in folder
     */
    PageableList<FileInfoVo> listFilesInFolder(@NotNull ListVFolderFilesReq req);

    /**
     * List owned vfolder brief
     */
    List<VFolderBrief> listOwnedVFolderBriefs(@NotEmpty String userNo);
}
