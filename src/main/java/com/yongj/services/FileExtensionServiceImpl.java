package com.yongj.services;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileExtensionMapper;
import com.yongj.vo.FileExtVo;
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

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<FileExtVo> getDetailsOfAll() {
        return BeanCopyUtils.toTypeList(fileExtensionMapper.findAll(), FileExtVo.class);
    }

    @Override
    public void updateFileExtSelective(FileExtVo fileExtVo) {
        FileExtension fe = BeanCopyUtils.toType(fileExtVo, FileExtension.class);
        fileExtensionMapper.updateSelective(fe);
    }

}
