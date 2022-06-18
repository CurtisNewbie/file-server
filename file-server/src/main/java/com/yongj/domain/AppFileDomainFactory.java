package com.yongj.domain;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.curtisnewbie.common.util.AssertUtils;
import com.yongj.dao.AppFile;
import com.yongj.dao.AppFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Factory for AppFileDomain
 *
 * @author yongj.zhuang
 */
@Component
public class AppFileDomainFactory {

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private AppFileMapper appFileMapper;

    public AppFileDomain empty() {
        return applicationContext.getBean(AppFileDomain.class);
    }

    public AppFileDomain forUuid(String uuid) {
        AssertUtils.notNull(uuid, "UUID == null");

        final AppFileDomain af = empty();
        final AppFile appFile = appFileMapper.selectOne(new LambdaQueryWrapper<AppFile>()
                .eq(AppFile::getUuid, uuid));
        AssertUtils.notNull(appFile, "AppFile for %s doesn't exist", uuid);
        return af._with(appFile);

    }
}
