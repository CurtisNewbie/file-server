package com.yongj.domain;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.curtisnewbie.common.domain.Domain;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.RandomUtils;
import com.yongj.dao.*;
import com.yongj.enums.VFOwnership;
import com.yongj.vo.VFolderWithOwnership;
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

    public static final String FOLDER_NO_PRE = "VFLD";
    private String userNo;
    private VFolder folder;
    private VFOwnership ownership;


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

    /** Add file to this VFolder */
    public void addFile(@NotEmpty String fileKey) {

        // only owner of the folder can do this
        _assertIsFolderOwner();

        // make sure the file is not in current VFolder
        if (!isFileNotInFolder(fileKey)) {
            log.info("File '{}' is already in folder", fileKey);
            return;
        }

        FileVFolder ff = new FileVFolder();
        ff.setFolderNo(this.folder.getFolderNo());
        ff.setUuid(fileKey);
        fileFolderMapper.insert(ff);
    }

    public boolean isOwner() {
        return ownership == VFOwnership.OWNER;
    }

    // ------------------------------- private ---------------------------------------------

    public VFolderDomain _forUser(String userNo) {
        AssertUtils.notNull(userNo, "userNo == null");
        this.userNo = userNo;
        return this;
    }

    public VFolderDomain _forFolder(String userNo, VFolderWithOwnership folder) {
        AssertUtils.notNull(userNo, "userNo == null");
        AssertUtils.notNull(folder, "VFolder == null");
        this.userNo = userNo;

        this.folder = BeanCopyUtils.toType(folder, VFolder.class);
        this.ownership = folder.getOwnership();
        return this;
    }

    private void _assertIsFolderOwner() {
        AssertUtils.isTrue(isOwner(), "Only owner can add files to this folder");
    }

    private boolean isFileNotInFolder(String fileKey) {
        final FileVFolder ff = fileFolderMapper.selectOne(Wrappers.lambdaQuery(FileVFolder.class)
                .select(FileVFolder::getId)
                .eq(FileVFolder::getFolderNo, this.folder.getFolderNo())
                .eq(FileVFolder::getUuid, fileKey));

        return ff == null;
    }

}
