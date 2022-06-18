package com.yongj.services;

import com.yongj.domain.AppFileDomain;
import com.yongj.domain.AppFileDomainFactory;
import com.yongj.vo.UploadAppFileCmd;
import feign.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author yongj.zhuang
 */
@Component
public class AppFileServiceImpl implements AppFileService {

    @Autowired
    private AppFileDomainFactory factory;

    @Override
    public String upload(UploadAppFileCmd cmd) throws IOException {
        final AppFileDomain domain = factory.empty();
        return domain.uploadAppFile(cmd);
    }

    @Override
    public Response download(String uuid) throws IOException {
        final AppFileDomain domain = factory.forUuid(uuid);
        final long lsize = domain.getSize();
        final Integer isize = lsize <= Integer.MAX_VALUE ? (int) lsize : null;

        return Response.builder()
                .body(domain.obtainInputStream(), isize)
                .build();
    }
}
