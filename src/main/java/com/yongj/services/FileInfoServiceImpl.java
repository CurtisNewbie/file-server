package com.yongj.services;

import com.yongj.vo.FileInfoVo;

import javax.servlet.ServletOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author yongjie.zhuang
 */
public class FileInfoServiceImpl implements FileInfoService {

    @Override
    public void saveFileInfo(int userId, String fileName, String userGroup, InputStream inputStream) {

    }

    @Override
    public List<FileInfoVo> findFilesForUser(int userId) {
        return null;
    }

    @Override
    public void downloadFile(String uuid, ServletOutputStream outputStream) {

    }
}
