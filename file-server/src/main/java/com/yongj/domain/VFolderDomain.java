package com.yongj.domain;

import com.curtisnewbie.common.domain.Domain;
import com.curtisnewbie.common.util.AssertUtils;
import com.yongj.dao.FileFolderMapper;
import com.yongj.dao.VFolder;
import com.yongj.dao.VFolderMapper;
import com.yongj.vo.CreateFolderCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

/**
 * VFolder Domain
 *
 * @author yongj.zhuang
 */
@Slf4j
@Domain
@Validated
public class VFolderDomain {

    private String userNo;
    private VFolder folder;

    private final FileFolderMapper fileFolderMapper;
    private final VFolderMapper vfolderMapper;

    public VFolderDomain(FileFolderMapper fileFolderMapper, VFolderMapper vfolderMapper) {
        this.fileFolderMapper = fileFolderMapper;
        this.vfolderMapper = vfolderMapper;
    }

    public void createFolder(CreateFolderCmd cmd) {
    }


    // ------------------------------- private ---------------------------------------------

    private VFolderDomain _with(String userNo, VFolder folder) {
        AssertUtils.notNull(userNo, "userNo == null");
        AssertUtils.notNull(folder, "VFolder == null");
        this.userNo = userNo;
        this.folder = folder;
        return this;
    }


}
