package com.yongj.vo;

import com.yongj.enums.*;
import lombok.*;

import java.util.*;

/**
 * @author yongj.zhuang
 */
@Data
public class FileDownloadValidInfo {

    /** file_info.id */
    private Integer fileId;

    /** the group that the file belongs to, 0-public, 1-private */
    private Integer userGroup;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** file_sharing.id */
    private Integer fileSharingId;

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    private Integer isLogicDeleted;

    /** File type */
    private FileType fileType;

    /** Is not a directory */
    public boolean isNotDir() {
        return fileType != FileType.DIR;
    }

    public boolean isDeleted() {
        if (isLogicDeleted == null)
            return false;

        return Objects.equals(FileLogicDeletedEnum.LOGICALLY_DELETED.getValue(), isLogicDeleted);
    }
}
