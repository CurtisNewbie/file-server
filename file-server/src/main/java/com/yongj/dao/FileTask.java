package com.yongj.dao;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "file_task")
public class FileTask extends DaoSkeleton {

    /** task no */
    @TableField("task_no")
    private String taskNo;

    /** user no */
    @TableField("user_no")
    private String userNo;

    /** task type */
    @TableField("type")
    private FileTaskType type;

    /** task status */
    @TableField("status")
    private FileTaskStatus status;

    /** task description */
    @TableField("description")
    private String description;

    /** file key */
    @TableField("file_key")
    private String fileKey;

    /** start time */
    @TableField("start_time")
    private LocalDateTime startTime;

    /** end time */
    @TableField("end_time")
    private LocalDateTime endTime;

    /** remark */
    @TableField("remark")
    private String remark;
}
