package com.yongj.vo.filetask;

import com.curtisnewbie.common.dao.DaoSkeleton;
import com.yongj.enums.FileTaskStatus;
import com.yongj.enums.FileTaskType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * File Task
 *
 * @author yongj.zhuang
 */
@Data
public class ListFileTaskVo extends DaoSkeleton {

    /** task no */
    private String taskNo;

    /** task type */
    private FileTaskType type;

    /** task status */
    private FileTaskStatus status;

    /** task description */
    private String description;

    /** file key */
    private String fileKey;

    /** start time */
    private LocalDateTime startTime;

    /** end time */
    private LocalDateTime endTime;

    /** remark */
    private String remark;
}
