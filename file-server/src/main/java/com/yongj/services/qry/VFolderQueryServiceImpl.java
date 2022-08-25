package com.yongj.services.qry;

import com.baomidou.mybatisplus.extension.plugins.pagination.*;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.*;
import com.yongj.dao.*;
import com.yongj.vo.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

import static com.curtisnewbie.common.util.PagingUtil.*;

/**
 * @author yongj.zhuang
 */
@Service
public class VFolderQueryServiceImpl implements VFolderQueryService {

    @Autowired
    private VFolderMapper vFolderMapper;

    @Override
    public PageableList<VFolderListResp> listVFolders(ListVFolderReq req) {
        final Page<VFolderListResp> page = vFolderMapper.listVFolders(forPage(req.getPagingVo()), req);
        return PageableList.from(page);
    }
}
