package com.yongj.services;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.curtisnewbie.common.dao.IsDel;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.yongj.dao.FileExtension;
import com.yongj.dao.FileExtensionMapper;
import com.yongj.vo.FileExtVo;
import com.yongj.vo.ListFileExtReqVo;
import com.yongj.vo.UpdateFileExtReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.curtisnewbie.common.util.AssertUtils.isNull;
import static com.curtisnewbie.common.util.AssertUtils.isTrue;
import static com.curtisnewbie.common.util.PagingUtil.forPage;
import static com.curtisnewbie.common.util.PagingUtil.toPageList;
import static java.lang.String.format;


/**
 * @author yongjie.zhuang
 */
@Service
@Transactional
public class FileExtensionServiceImpl implements FileExtensionService {

    private final Pattern FILE_EXT_PATTERN = Pattern.compile("^[a-zA-Z0-9]+$");

    @Autowired
    private FileExtensionMapper fileExtensionMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getNamesOfAllEnabled() {
        return fileExtensionMapper.findNamesOfAllEnabled();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PageablePayloadSingleton<List<FileExtVo>> getDetailsOfAllByPageSelective(@NotNull ListFileExtReqVo param) {
        Assert.notNull(param.getPagingVo(), "PagingVo can't be null");

        return toPageList(
                fileExtensionMapper.findAllSelective(forPage(param.getPagingVo()), BeanCopyUtils.toType(param, FileExtension.class)),
                v -> BeanCopyUtils.toType(v, FileExtVo.class)
        );
    }

    @Override
    public void updateFileExtension(@NotNull UpdateFileExtReq req) {
        Objects.requireNonNull(req.getId());

        final FileExtension param = new FileExtension();
        param.setIsEnabled(req.getIsEnabled());

        final QueryWrapper<FileExtension> condition = new QueryWrapper<FileExtension>()
                .eq("id", req.getId())
                .eq("is_del", IsDel.NORMAL.getValue());

        fileExtensionMapper.update(param, condition);
    }

    @Override
    public void addFileExt(@NotNull FileExtension fileExtension) {
        Objects.requireNonNull(fileExtension.getName());
        isTrue(FILE_EXT_PATTERN.matcher(fileExtension.getName()).matches(), format("File extension '%s' format illegal", fileExtension.getName()));
        isNull(fileExtensionMapper.findIdByName(fileExtension.getName()), format("File extension '%s' already exists", fileExtension.getName()));

        fileExtensionMapper.insert(fileExtension);
    }

    @Override
    public boolean isEnabled(String fileExt) {
        return fileExtensionMapper.getIdOfEnabledFileExt(fileExt) != null;
    }

}
