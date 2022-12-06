package com.yongj.helper;

/**
 * Helper for VFolder and files
 *
 * @author yongj.zhuang
 */
public interface VFolderAddFileOpHelper {

    /**
     * Pre validation of files being added to vfolder
     *
     * @return whether the file can be added
     */
    boolean preAddFileToVFolder(String folderNo, String fileKey, int userId, String userNo);
}
