package com.yongj.vo;

import com.curtisnewbie.common.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yongj.enums.FsGroupType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * FileSystem group, used to differentiate which base folder or mounted folder should be used
 *
 * @author yongjie.zhuang
 */
@Data
public class FsGroupVo {

    private Integer id;

    /** group name */
    private String name;

    /** base folder */
    private String baseFolder;

    /** mode: 1-read, 2-read/write */
    private Integer mode;

    /** Type of a fs_group */
    private FsGroupType type;

    /** when the record is updated */
    @JsonFormat(pattern = DateUtils.DD_MM_YYYY_HH_MM)
    private LocalDateTime updateTime;

    /** who updated this record */
    private String updateBy;
}