package com.yongj.converters;

import com.yongj.dao.FileSharing;
import com.yongj.vo.FileSharingVo;
import com.yongj.vo.FileSharingWebVo;
import org.mapstruct.Mapper;

/**
 * @author yongjie.zhuang
 */
@Mapper(componentModel = "spring")
public interface FileSharingConverter {

    FileSharingVo toVo(FileSharing fs);

    FileSharingWebVo toWebVo(FileSharingVo fsv);
}
