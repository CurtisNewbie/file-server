package com.yongj.services;

import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.LockUtils;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.domain.VFolderDomain;
import com.yongj.helper.VFolderAddFileOpHelper;
import com.yongj.repository.VFolderRepository;
import com.yongj.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.locks.Lock;

/**
 * @author yongj.zhuang
 */
@Slf4j
@Service
public class VFolderServiceImpl implements VFolderService {

    @Autowired
    private VFolderRepository vFolderRepository;
    @Autowired
    private RedisController redisctl;
    @Autowired
    private VFolderAddFileOpHelper vfolderAddFileOpHelper;

    @Override
    public String createVFolder(CreateVFolderCmd cmd) {
        final Lock lock = redisctl.getLock("vfolder:user:" + cmd.getUserNo());
        return LockUtils.lockAndCall(lock, () -> {
            return vFolderRepository.createVFolder(cmd.getUserNo(), cmd.getName());
        });
    }

    @Override
    public void addFileToVFolder(AddFileToVFolderCmd cmd) {
        var fkeys = cmd.getFileKeys();
        final Lock lock = _folderLock(cmd.getFolderNo());
        LockUtils.lockAndRun(lock, () -> {
            final VFolderDomain domain = vFolderRepository.findVFolder(cmd.getUserNo(), cmd.getFolderNo());
            AssertUtils.isTrue(domain.isOwner(), "Only owner is permitted");

            fkeys.stream()
                    .distinct()
                    .filter(fk -> vfolderAddFileOpHelper.preAddFileToVFolder(cmd.getFolderNo(), fk, cmd.getUserId(), cmd.getUserNo()))
                    .peek(fk -> log.info("Adding file {} to folder: {}", fk, cmd.getFolderNo()))
                    .forEach(domain::addFile);
        });
    }

    @Override
    public void removeFileFromVFolder(RemoveFileFromVFolderCmd cmd) {
        final Lock lock = _folderLock(cmd.getFolderNo());
        LockUtils.lockAndRun(lock, () -> {
            final VFolderDomain domain = vFolderRepository.findVFolder(cmd.getUserNo(), cmd.getFolderNo());
            AssertUtils.isTrue(domain.isOwner(), "Only owner is permitted");
            cmd.getFileKeys().stream().distinct().forEach(domain::removeFile);
        });
    }

    @Override
    public void shareVFolder(ShareVFolderCmd cmd) {
        if (Objects.equals(cmd.getCurrUserNo(), cmd.getSharedToUserNo())) return;

        final Lock lock = _folderLock(cmd.getFolderNo());
        LockUtils.lockAndRun(lock, () -> {
            final VFolderDomain domain = vFolderRepository.findVFolder(cmd.getCurrUserNo(), cmd.getFolderNo());
            AssertUtils.isTrue(domain.isOwner(), "Only owner is permitted");
            domain.shareTo(cmd.getSharedToUserNo());
        });
    }

    @Override
    public void removeGrantedAccess(RemoveGrantedVFolderAccessCmd cmd) {
        if (Objects.equals(cmd.getCurrUserNo(), cmd.getSharedToUserNo())) return;

        final Lock lock = _folderLock(cmd.getFolderNo());
        LockUtils.lockAndRun(lock, () -> {
            final VFolderDomain domain = vFolderRepository.findVFolder(cmd.getCurrUserNo(), cmd.getFolderNo());
            AssertUtils.isTrue(domain.isOwner(), "Only owner is permitted");
            domain.removeGrantedAccess(cmd.getSharedToUserNo());
        });
    }

    private Lock _folderLock(String folderNo) {
        return redisctl.getLock("vfolder:" + folderNo);
    }
}
