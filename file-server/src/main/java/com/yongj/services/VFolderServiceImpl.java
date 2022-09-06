package com.yongj.services;

import com.curtisnewbie.common.util.LockUtils;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.domain.VFolderDomain;
import com.yongj.helper.*;
import com.yongj.repository.VFolderRepository;
import com.yongj.vo.AddFileToVFolderCmd;
import com.yongj.vo.CreateVFolderCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private FileTypeResolver fileTypeResolver;

    @Override
    @Transactional
    public String createVFolder(CreateVFolderCmd cmd) {
        final Lock lock = redisctl.getLock(_lockKey(cmd.getUserNo()));
        return LockUtils.lockAndCall(lock, () -> {
            final VFolderDomain domain = vFolderRepository.buildForNewVFolder(cmd.getUserNo(), cmd.getName());
            return domain.createFolder(cmd.getName(), cmd.getUsername());
        });
    }

    @Override
    public void addFileToVFolder(AddFileToVFolderCmd cmd) {
        final Lock lock = redisctl.getLock(_lockKey(cmd.getUserNo()));
        LockUtils.lockAndRun(lock, () -> {
            final VFolderDomain domain = vFolderRepository.buildVFolder(cmd.getUserNo(), cmd.getFolderNo());
            cmd.getFileKeys().stream()
                    .filter(fileTypeResolver::isFile)
                    .forEach(domain::addFile);
        });
    }

    private static String _lockKey(String userNo) {
        return "vfolder:user:" + userNo;
    }
}
