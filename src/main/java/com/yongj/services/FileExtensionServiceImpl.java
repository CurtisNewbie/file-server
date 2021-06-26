package com.yongj.services;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileExtensionMapper;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

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
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageInfo<FileExtVo> getDetailsOfAllByPageSelective(@NotNull ListFileExtReqVo param) {
        Objects.requireNonNull(param.getPagingVo());
        PageHelper.startPage(param.getPagingVo().getPage(), param.getPagingVo().getLimit());
        return BeanCopyUtils.toPageList(
                PageInfo.of(fileExtensionMapper.findAllSelective(BeanCopyUtils.toType(param, FileExtension.class))
                ), FileExtVo.class
        );
    }

    @Override
    public void updateFileExtSelective(@NotNull FileExtVo fileExtVo) {
        Objects.requireNonNull(fileExtVo.getId());
        FileExtension fe = BeanCopyUtils.toType(fileExtVo, FileExtension.class);
        fileExtensionMapper.updateSelective(fe);
    }

}
