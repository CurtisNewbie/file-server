package com.yongj.services;

import com.yongj.domain.AppFileDomain;
import com.yongj.repository.AppFileRepository;
import com.yongj.vo.AppFileDownloadInfo;
import com.yongj.vo.UploadAppFileCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yongj.zhuang
 */
@Slf4j
@Component
public class AppFileServiceImpl implements AppFileService {

    @Autowired
    private AppFileRepository appFileRepository;

    @Override
    public String upload(UploadAppFileCmd cmd) throws IOException {
        final AppFileDomain domain = appFileRepository.buildEmpty();
        return domain.uploadAppFile(cmd);
    }

    @Override
    public AppFileDownloadInfo download(String uuid) throws IOException {
        final AppFileDomain domain = appFileRepository.buildForUuid(uuid);
        return domain.obtainDownloadInfo();
    }
}
