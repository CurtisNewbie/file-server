package com.yongj.services.qry;

import com.curtisnewbie.common.vo.*;
import com.yongj.vo.*;
import org.springframework.validation.annotation.*;

import javax.validation.constraints.*;

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
    PageableList<ListFileInfoRespVo> listFilesInFolder(@NotNull ListVFolderFilesReq req);
}
