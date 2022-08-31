package com.yongj.vo;

import com.curtisnewbie.common.dao.DaoSkeleton;
import com.yongj.enums.VFOwnership;
import lombok.Data;

/**
 * Virtual folder
 *
 * @author yongj.zhuang
 */
@Data
public class VFolderWithOwnership extends DaoSkeleton {

    /** folder no */
    private String folderNo;

    /** name of the folder */
    private String name;

    /** folder ownership */
    private VFOwnership ownership;

}
