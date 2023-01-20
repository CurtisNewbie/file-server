package com.yongj.listeners;

import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.LockUtils;
import com.curtisnewbie.module.messaging.listener.MsgListener;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.domain.FileTaskDomain;
import com.yongj.enums.MqConst;
import com.yongj.repository.FileTaskRepository;
import com.yongj.vo.event.FileDeletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yongj.zhuang
 */
@Slf4j
@Component
public class FileDeletedEventListener {

    @Autowired
    private FileTaskRepository fileTaskRepository;
    @Autowired
    private RedisController redisController;

    @MsgListener(exchange = MqConst.FILE_DELETED_EVENT_EXG, queue = MqConst.FILE_DELETED_EVENT_QUE)
    public void onFileDeleted(FileDeletedEvent evt) {
        log.info("Received evt: {}", evt);
        final String taskNo = fileTaskRepository.lookupTaskNoByKey(evt.getFileKey());
        if (taskNo == null)
            return;

        var lock = redisController.getLock(FileTaskDomain.lockKey(taskNo));
        LockUtils.lockAndRun(lock, () -> {
            final FileTaskDomain fileTaskDomain = fileTaskRepository.findByTaskNo(taskNo);
            AssertUtils.notNull(fileTaskDomain, "fileTaskDomain is null, taskNo: %s", taskNo);
            fileTaskDomain.removeFileKey();
        });
    }

}
