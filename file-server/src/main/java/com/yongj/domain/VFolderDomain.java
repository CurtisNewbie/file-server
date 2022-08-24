package com.yongj.domain;

import com.curtisnewbie.common.domain.Domain;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.RandomUtils;
import com.yongj.dao.*;
import com.yongj.enums.VFOwnership;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * VFolder Domain
 *
 * @author yongj.zhuang
 */
@Slf4j
@Domain
@Validated
public class VFolderDomain {

    public static String FOLDER_NO_PRE = "VFLD";
    private String userNo;
    private VFolder folder;


    /*
    Autowired Components
     */
    private final FileVFolderMapper fileFolderMapper;
    private final VFolderMapper vfolderMapper;
    private final UserVFolderMapper userVFolderMapper;

    public VFolderDomain(FileVFolderMapper fileFolderMapper, VFolderMapper vfolderMapper, UserVFolderMapper userVFolderMapper) {
        this.fileFolderMapper = fileFolderMapper;
        this.vfolderMapper = vfolderMapper;
        this.userVFolderMapper = userVFolderMapper;
    }

    /**
     * Create a new folder for current user
     *
     * @return folderNo
     */
    public String createFolder(@NotEmpty String name, @NotEmpty String username) {
        final String folderNo = RandomUtils.sequence(FOLDER_NO_PRE, 15);

        // for the vfolder
        folder = new VFolder();
        folder.setName(name);
        folder.setFolderNo(folderNo);
        vfolderMapper.insert(folder);

        // for the user - vfolder relation
        final UserVFolder relation = new UserVFolder();
        relation.setFolderNo(folderNo);
        relation.setUserNo(this.userNo);
        relation.setOwnership(VFOwnership.OWNER);
        relation.setGrantedBy(username);
        userVFolderMapper.insert(relation);
        return folderNo;
    }


    // ------------------------------- private ---------------------------------------------

    public VFolderDomain _with(String userNo, VFolder folder) {
        AssertUtils.notNull(userNo, "userNo == null");
        AssertUtils.notNull(folder, "VFolder == null");
        this.userNo = userNo;
        this.folder = folder;
        return this;
    }


}
