package com.yongj.helper;

import com.yongj.dao.FileInfo;
import com.yongj.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author yongj.zhuang
 */
@Component
public class VFolderAddFileOpHelperImpl implements VFolderAddFileOpHelper {

    @Autowired
    private FileService fileService;

    @Override
    public boolean preAddFileToVFolder(String folderNo, String fileKey, int userId, String userNo) {
        final FileInfo fi = fileService.findByKey(fileKey);
        if (fi == null) return false;
        if (!fi.belongsTo(userId)) return false;
        return fi.isFile();
    }
}
