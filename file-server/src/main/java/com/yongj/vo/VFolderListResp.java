package com.yongj.vo;

import com.yongj.enums.VFOwnership;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Virtual folder
 *
 * @author yongj.zhuang
 */
@Data
public class VFolderListResp {

    private Integer id;

    /** when the record is created */
    private LocalDateTime createTime;

    /** who created this record */
    private String createBy;

    /** when the record is updated */
    private LocalDateTime updateTime;

    /** who updated this record */
    private String updateBy;

    /** folder no */
    private String folderNo;

    /** name of the folder */
    private String name;

    /** ownership */
    private VFOwnership ownership;

}
