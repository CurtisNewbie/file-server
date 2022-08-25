package com.yongj.services.qry;

import com.curtisnewbie.common.vo.*;
import com.yongj.vo.*;

import javax.validation.constraints.*;

/**
 * Query Service for VFolder
 *
 * @author yongj.zhuang
 */
public interface VFolderQueryService {

    /**
     * List vfolders
     */
    PageableList<VFolderListResp> listVFolders(@NotNull ListVFolderReq req);

}
