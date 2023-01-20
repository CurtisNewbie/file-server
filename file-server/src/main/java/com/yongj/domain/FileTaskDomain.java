package com.yongj.domain;

import com.curtisnewbie.common.domain.Domain;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.common.util.ObjectDiff;
import com.curtisnewbie.common.util.StrUtils;
import com.yongj.dao.FileTask;
import com.yongj.dao.FileTaskMapper;
import com.yongj.enums.FileTaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * File Task Domain
 *
 * @author yongj.zhuang
 */
@Slf4j
@Domain
@Validated
public class FileTaskDomain {

    private FileTask fileTask;

    @Autowired
    private FileTaskMapper fileTaskMapper;

    public void startTask() {
        AssertUtils.equals(fileTask.getStatus(), FileTaskStatus.INIT, "Only INIT FileTask can be started");
        log.info("FileTask started {}", fileTask.getTaskNo());
        _updateTask(FileTaskStatus.PROCESSING, null, null);
    }

    public boolean isEnded() {
        return this.fileTask.getStatus().isEndState;
    }

    public boolean isStarted() {
        return this.fileTask.getStatus() != FileTaskStatus.INIT;
    }

    public void taskFinished(@Nullable String fileKey) {
        AssertUtils.equals(fileTask.getStatus(), FileTaskStatus.PROCESSING, "Only PROCESSING FileTask can be finished");
        log.info("FileTask finished {}", fileTask.getTaskNo());
        _updateTask(FileTaskStatus.FINISHED, fileKey, null);
    }

    public void taskInterrupted() {
        AssertUtils.equals(fileTask.getStatus(), FileTaskStatus.PROCESSING, "Only PROCESSING FileTask can be interrupted");
        log.info("FileTask interrupted {}", fileTask.getTaskNo());
        _updateTask(FileTaskStatus.INTERRUPTED, null, null);
    }

    public void taskFailed(@Nullable String remark) {
        AssertUtils.equals(fileTask.getStatus(), FileTaskStatus.PROCESSING, "Only PROCESSING FileTask can be failed");
        log.info("FileTask failed {}", fileTask.getTaskNo());
        _updateTask(FileTaskStatus.FAILED, null, remark);
    }

    private void _updateTask(FileTaskStatus status, @Nullable String fileKey, @Nullable String remark) {
        FileTask update = new FileTask();
        update.setId(fileTask.getId());
        update.setStatus(status);
        update.setFileKey(fileKey);
        update.setRemark(StrUtils.shrink(remark, 128));
        if (status == FileTaskStatus.PROCESSING)
            update.setStartTime(LocalDateTime.now());
        else
            update.setEndTime(LocalDateTime.now());
        fileTaskMapper.updateById(update);
        ObjectDiff.from(update).applyDiffTo(fileTask);
    }

    public FileTaskDomain _with(FileTask ft) {
        this.fileTask = ft;
        return this;
    }

    public void removeFileKey() {
        if (StringUtils.hasText(this.fileTask.getFileKey())) {
            FileTask update = new FileTask();
            update.setId(fileTask.getId());
            update.setFileKey("");
            update.setRemark("File has been deleted");
            fileTaskMapper.updateById(update);
            ObjectDiff.from(update).applyDiffTo(fileTask);
        }
    }

    public static String lockKey(String taskNo) {
        return "file:service:file:task:" + taskNo;
    }
}
