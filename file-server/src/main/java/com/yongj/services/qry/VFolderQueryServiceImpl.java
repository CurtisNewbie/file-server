package com.yongj.services.qry;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.VFolderMapper;
import com.yongj.vo.FileInfoVo;
import com.yongj.vo.ListVFolderFilesReq;
import com.yongj.vo.ListVFolderReq;
import com.yongj.vo.VFolderListResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        final Page<VFolderListResp> page = vFolderMapper.listVFolders(forPage(req.getPagingVo()), req);
        return PageableList.from(page);
    }

    @Override
    public PageableList<FileInfoVo> listFilesInFolder(ListVFolderFilesReq req) {
        final Page<FileInfoVo> page = vFolderMapper.listFilesInVFolders(forPage(req.getPagingVo()), req);
        page.getRecords().forEach(v -> v.setIsOwner(Objects.equals(v.getUploaderId(), req.getUserId())));
        return PageableList.from(page);
    }

}
