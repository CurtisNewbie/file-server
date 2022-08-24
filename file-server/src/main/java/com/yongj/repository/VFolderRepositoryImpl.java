package com.yongj.repository;

import com.curtisnewbie.common.util.AssertUtils;
import com.yongj.dao.UserVFolderMapper;
import com.yongj.dao.VFolder;
import com.yongj.dao.VFolderMapper;
import com.yongj.domain.VFolderDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

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
    public VFolderDomain buildVFolder(String userNo, String folderNo) {
        final VFolder vfolder = vFolderMapper.findVFolderForUser(userNo, folderNo);
        AssertUtils.notNull(vfolder, "Folder not found");

        return buildEmpty()._with(userNo, vfolder);
    }

    @Override
    public VFolderDomain buildForNewVFolder(String userNo, String name) {
        final Integer id = vFolderMapper.findIdForFolderWithName(userNo, name);
        AssertUtils.isNull(id, "Found folder with the same name");

        return buildEmpty();
    }

    private VFolderDomain buildEmpty() {
        return applicationContext.getBean(VFolderDomain.class);
    }

}
