package com.yongj.helper;

import com.yongj.enums.*;
import com.yongj.services.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

/**
 * @author yongj.zhuang
 */
@Component
public class DefaultFileTypeResolver implements FileTypeResolver {

    @Autowired
    private FileService fileService;

    @Override
    public FileType resolve(String uuid) {
        return fileService.findFileTypeByKey(uuid);
    }
}
