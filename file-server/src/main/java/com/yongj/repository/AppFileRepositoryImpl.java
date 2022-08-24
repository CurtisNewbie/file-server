package com.yongj.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.curtisnewbie.common.util.AssertUtils;
import com.yongj.dao.AppFile;
import com.yongj.dao.AppFileMapper;
import com.yongj.domain.AppFileDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

/**
 * @author yongj.zhuang
 */
@Repository
public class AppFileRepositoryImpl implements AppFileRepository {

    @Autowired
    private AppFileMapper appFileMapper;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public AppFileDomain buildForUuid(String uuid) {
        AssertUtils.notNull(uuid, "UUID == null");

        final AppFileDomain af = buildEmpty();
        final AppFile appFile = appFileMapper.selectOne(new LambdaQueryWrapper<AppFile>()
                .eq(AppFile::getUuid, uuid));
        AssertUtils.notNull(appFile, "AppFile for %s doesn't exist", uuid);
        return af._with(appFile);
    }

    @Override
    public AppFileDomain buildEmpty() {
        return applicationContext.getBean(AppFileDomain.class);
    }
}
