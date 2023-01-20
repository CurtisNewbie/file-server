package com.yongj.repository;

import com.curtisnewbie.common.util.IdUtils;
import com.curtisnewbie.common.util.MapperUtils;
import com.curtisnewbie.common.util.StrUtils;
import com.yongj.dao.FileTask;
import com.yongj.dao.FileTaskMapper;
import com.yongj.domain.FileTaskDomain;
import com.yongj.enums.FileTaskStatus;
import com.yongj.enums.FileTaskType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

/**
 * @author yongj.zhuang
 */
@Slf4j
@Repository
public class FileTaskRepositoryImpl implements FileTaskRepository {

    public static final String PRE = "FT";

    @Autowired
    private FileTaskMapper fileTaskMapper;
    @Autowired
    private ApplicationContext appCtx;

    @Override
    public FileTaskDomain createFileTask(String userNo, FileTaskType type, String desc) {
        FileTask ft = new FileTask();
        ft.setTaskNo(IdUtils.gen(PRE));
        ft.setUserNo(userNo);
        ft.setType(type);
        ft.setStatus(FileTaskStatus.INIT);
        ft.setDescription(StrUtils.shrink(desc, 100));
        fileTaskMapper.insert(ft);
        log.info("Created FileTask, userNo: {}, type: {}, desc: {}, taskNo: {}, id: {}", userNo, type, desc, ft.getTaskNo(), ft.getId());

        final FileTaskDomain ftd = appCtx.getBean(FileTaskDomain.class);
        return ftd._with(ft);
    }

    @Override
    public FileTaskDomain findByTaskNo(String taskNo) {
        var ft = fileTaskMapper.selectOne(MapperUtils.eq(FileTask::getTaskNo, taskNo));
        if (ft == null) return null;
        return appCtx.getBean(FileTaskDomain.class)._with(ft);
    }

    @Override
    public String lookupTaskNoByKey(String fileKey) {
        var ft = fileTaskMapper.selectOne(MapperUtils.eq(FileTask::getFileKey, fileKey).last("limit 1"));
        return ft != null ? ft.getTaskNo() : null;
    }
}
