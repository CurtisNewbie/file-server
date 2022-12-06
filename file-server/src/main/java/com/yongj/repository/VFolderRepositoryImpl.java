package com.yongj.repository;

import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.IdUtils;
import com.yongj.dao.UserVFolder;
import com.yongj.dao.UserVFolderMapper;
import com.yongj.dao.VFolder;
import com.yongj.dao.VFolderMapper;
import com.yongj.domain.VFolderDomain;
import com.yongj.enums.VFOwnership;
import com.yongj.vo.VFolderWithOwnership;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.yongj.domain.VFolderDomain.FOLDER_NO_PRE;

/**
 * @author yongj.zhuang
 */
@Slf4j
@Repository
public class VFolderRepositoryImpl implements VFolderRepository {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private VFolderMapper vFolderMapper;
    @Autowired
    private UserVFolderMapper userVFolderMapper;

    @Override
    public VFolderDomain findVFolder(String userNo, String folderNo) {
        final VFolderWithOwnership vf = vFolderMapper.findVFolderWithOwnership(userNo, folderNo);
        AssertUtils.notNull(vf, "Folder not found");

        return buildEmpty()._forFolder(userNo, vf);
    }

    @Override
    @Transactional
    public String createVFolder(String userNo, String name) {
        final Integer id = vFolderMapper.findIdForFolderWithName(userNo, name);
        AssertUtils.isNull(id, String.format("Found folder with same name ('%s')", name));

        final String folderNo = IdUtils.gen(FOLDER_NO_PRE);

        // for the vfolder
        VFolder folder = new VFolder();
        folder.setName(name);
        folder.setFolderNo(folderNo);
        vFolderMapper.insert(folder);

        // for the user - vfolder relation
        final UserVFolder relation = new UserVFolder();
        relation.setFolderNo(folderNo);
        relation.setUserNo(userNo);
        relation.setOwnership(VFOwnership.OWNER);
        relation.setGrantedBy(userNo);
        userVFolderMapper.insert(relation);

        return folderNo;
    }

    private VFolderDomain buildEmpty() {
        return applicationContext.getBean(VFolderDomain.class);
    }

}
