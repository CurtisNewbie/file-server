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
    private FUserGroup userGroup;

    /** uploader id, i.e., user.id */
    private Integer uploaderId;

    /** whether the file is logically deleted, 0-normal, 1-deleted */
    private FLogicDelete isLogicDeleted;

    /** File type */
    private FileType fileType;

    /** file_sharing.id */
    private Integer fileSharingId;

    /** Is not a directory */
    public boolean isNotDir() {
        return fileType != FileType.DIR;
    }

    public boolean isDeleted() {
        return isLogicDeleted == FLogicDelete.DELETED;
    }

    public boolean isPublicGroup() {
        return Objects.equals(userGroup, FUserGroup.PUBLIC);
    }
}
