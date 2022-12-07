package com.yongj.services.qry;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.curtisnewbie.common.util.*;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.*;
import com.yongj.enums.*;
import com.yongj.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.function.*;
import java.util.stream.*;

import static com.curtisnewbie.common.util.PagingUtil.forPage;

/**
 * @author yongj.zhuang
 */
@Service
public class VFolderQueryServiceImpl implements VFolderQueryService {

    @Autowired
    private VFolderMapper vFolderMapper;
    private final Function<UserVFolder, GrantedFolderAccess> grantedFolderAccessConverter = uv -> {
        GrantedFolderAccess gfa = new GrantedFolderAccess();
        gfa.setUserNo(uv.getUserNo());
        gfa.setCreateTime(uv.getCreateTime());
        return gfa;
    };

    @Override
    public PageableList<VFolderListResp> listVFolders(ListVFolderReq req) {
        final Page<VFolderListResp> page = vFolderMapper.listVFolders(req.page(), req);
        return PageableList.from(page);
    }

    @Override
    public PageableList<FileInfoWebVo> listFilesInFolder(ListVFolderFilesReq req) {
        final Page<FileInfoWebVo> page = vFolderMapper.listFilesInVFolders(req.page(), req);
        page.getRecords().forEach(v -> v.setIsOwner(Objects.equals(v.getUploaderId(), req.getUserId())));
        return PageableList.from(page);
    }

    @Override
    public List<VFolderBrief> listOwnedVFolderBriefs(String userNo) {
        return vFolderMapper.listOwnedVFolderBrief(userNo);
    }

    @Override
    public PageableList<GrantedFolderAccess> listGrantedAccess(ListGrantedFolderAccessReq req, String userNo) {
        final String folderNo = req.getFolderNo();
        final VFolderWithOwnership vfo = vFolderMapper.findVFolderWithOwnership(userNo, folderNo);
        AssertUtils.notNull(vfo, "Not permitted");
        AssertUtils.isTrue(vfo.getOwnership() == VFOwnership.OWNER, "Not permitted");

        final Page<UserVFolder> ufp = vFolderMapper.listGrantedAccess(req.page(), folderNo);
        return PagingUtil.toPageableList(ufp, grantedFolderAccessConverter);
    }

}
