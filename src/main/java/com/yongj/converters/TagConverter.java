package com.yongj.converters;

import com.yongj.dao.Tag;
import com.yongj.vo.TagVo;
import com.yongj.vo.TagWebVo;
import org.mapstruct.Mapper;

/**
 * Converter for Tag
 *
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface TagConverter {

    TagVo toVo(Tag t);

    TagWebVo toWebVo(TagVo tv);
}
