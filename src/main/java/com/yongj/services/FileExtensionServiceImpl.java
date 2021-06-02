package com.yongj.services;

import com.yongj.dao.FileExtensionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class FileExtensionServiceImpl implements FileExtensionService {

    @Autowired
    private FileExtensionMapper fileExtensionMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getNamesOfAllEnabled() {
        return fileExtensionMapper.findNamesOfAllEnabled();
    }

}
