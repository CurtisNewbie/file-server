package com.yongj.domain;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.curtisnewbie.common.domain.Domain;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.MapperUtils;
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

    /** Remove file from vfolder */
    public void removeFile(@NotEmpty String fileKey) {
        if (isFileNotInFolder(fileKey)) {
            log.info("File '{}' is not in folder", fileKey);
            return;
        }

        fileFolderMapper.delete(MapperUtils
                .eq(FileVFolder::getFolderNo, this.folder.getFolderNo())
                .eq(FileVFolder::getUuid, fileKey));
    }

    /** Add file to this VFolder */
    public void addFile(@NotEmpty String fileKey) {
        // make sure the file is not in current VFolder
        if (!isFileNotInFolder(fileKey)) {
            log.info("File '{}' is already in folder", fileKey);
            return;
        }

        FileVFolder ff = new FileVFolder();
        ff.setFolderNo(this.folder.getFolderNo());
        ff.setUuid(fileKey);
        fileFolderMapper.insert(ff);
        log.info("File {} added to folder: {}", fileKey, this.folder.getFolderNo());
    }

    /** Check whether current user is owner of the vfolder */
    public boolean isOwner() {
        return ownership == VFOwnership.OWNER;
    }

    /** Share vfolder to user */
    public void shareTo(@NotEmpty String sharedToUserNo) {
        final boolean isSharedAlready = userVFolderMapper.selectOne(MapperUtils
                .select(UserVFolder::getId)
                .eq(UserVFolder::getFolderNo, folder.getFolderNo())
                .eq(UserVFolder::getUserNo, sharedToUserNo)
                .last("limit 1")) != null;
        if (isSharedAlready) {
            log.info("VFolder is shared already, folderNo: {}, sharedTo: {}", this.folder.getFolderNo(), sharedToUserNo);
            return;
        }

        // for the user - vfolder relation
        final UserVFolder relation = new UserVFolder();
        relation.setFolderNo(this.folder.getFolderNo());
        relation.setUserNo(sharedToUserNo);
        relation.setOwnership(VFOwnership.GRANTED);
        relation.setGrantedBy(this.userNo);
        userVFolderMapper.insert(relation);
        log.info("VFolder shared to {} by {}, folderNo: {}", sharedToUserNo, this.userNo, this.folder.getFolderNo());
    }

    /** Remove granted access to vfolder */
    public void removeGrantedAccess(String sharedToUserNo) {
        userVFolderMapper.delete(MapperUtils
                .eq(UserVFolder::getFolderNo, folder.getFolderNo())
                .eq(UserVFolder::getUserNo, sharedToUserNo)
                .eq(UserVFolder::getOwnership, VFOwnership.GRANTED));
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

    private boolean isFileNotInFolder(String fileKey) {
        final FileVFolder ff = fileFolderMapper.selectOne(Wrappers.lambdaQuery(FileVFolder.class)
                .select(FileVFolder::getId)
                .eq(FileVFolder::getFolderNo, this.folder.getFolderNo())
                .eq(FileVFolder::getUuid, fileKey));

        return ff == null;
    }

}
