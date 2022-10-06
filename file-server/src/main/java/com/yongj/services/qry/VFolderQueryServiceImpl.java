package com.yongj.services.qry;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.VFolderMapper;
import com.yongj.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.curtisnewbie.common.util.PagingUtil.forPage;

/**
 * @author yongj.zhuang
 */
@Service
public class VFolderQueryServiceImpl implements VFolderQueryService {

    @Autowired
    private VFolderMapper vFolderMapper;

    @Override
    public PageableList<VFolderListResp> listVFolders(ListVFolderReq req) {
        final Page<VFolderListResp> page = vFolderMapper.listVFolders(req.page(), req);
        return PageableList.from(page);
    }

    @Override
    public PageableList<FileInfoVo> listFilesInFolder(ListVFolderFilesReq req) {
        final Page<FileInfoVo> page = vFolderMapper.listFilesInVFolders(req.page(), req);
        page.getRecords().forEach(v -> v.setIsOwner(Objects.equals(v.getUploaderId(), req.getUserId())));
        return PageableList.from(page);
    }

    @Override
    public List<VFolderBrief> listOwnedVFolderBriefs(String userNo) {
        return vFolderMapper.listOwnedVFolderBrief(userNo);
    }

}
