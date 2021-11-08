package com.yongj.services;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.yongj.converters.FileExtConverter;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileExtensionMapper;
import com.yongj.exceptions.DuplicateExtException;
import com.yongj.exceptions.IllegalExtException;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageList;


/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class FileExtensionServiceImpl implements FileExtensionService {

    private final Pattern FILE_EXT_PATTERN = Pattern.compile("[a-zA-Z0-9]+");

    @Autowired
    private FileExtensionMapper fileExtensionMapper;
    @Autowired
    private FileExtConverter fileExtConverter;

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
    public PageablePayloadSingleton<List<FileExtVo>> getDetailsOfAllByPageSelective(@NotNull ListFileExtReqVo param) {
        Assert.notNull(param.getPagingVo(), "PagingVo can't be null");

        return toPageList(
                fileExtensionMapper.findAllSelective(forPage(param.getPagingVo()), fileExtConverter.toDo(param)),
                fileExtConverter::toVo
        );
    }

    @Override
    public void updateFileExtSelective(@NotNull FileExtVo fileExtVo) {
        Objects.requireNonNull(fileExtVo.getId());
        FileExtension fe = fileExtConverter.toDo(fileExtVo);
        fileExtensionMapper.updateSelective(fe);
    }

    @Override
    public void addFileExt(@NotNull FileExtension fileExtension) {
        Objects.requireNonNull(fileExtension.getName());
        if (!FILE_EXT_PATTERN.matcher(fileExtension.getName()).matches())
            throw new IllegalExtException("File extension '" + fileExtension.getName() + "' format illegal");
        if (fileExtensionMapper.findIdByName(fileExtension.getName()) != null)
            throw new DuplicateExtException("File extension '" + fileExtension.getName() + "' already exists");
        fileExtensionMapper.insert(fileExtension);
    }

}
