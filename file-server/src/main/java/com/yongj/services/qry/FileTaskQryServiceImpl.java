package com.yongj.services.qry;

import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.MapperUtils;
import com.curtisnewbie.common.vo.PageableList;
import com.yongj.dao.FileTask;
import com.yongj.dao.FileTaskMapper;
import com.yongj.vo.filetask.ListFileTaskReq;
import com.yongj.vo.filetask.ListFileTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yongj.zhuang
 */
@Service
public class FileTaskQryServiceImpl implements FileTaskQryService {

    @Autowired
    private FileTaskMapper fileTaskMapper;

    @Override
    public PageableList<ListFileTaskVo> listFileTasks(String userNo, ListFileTaskReq req) {
        return fileTaskMapper.selectPageAndConvert(MapperUtils.eq(FileTask::getUserNo, userNo).orderByDesc(FileTask::getId),
                req.page(),
                ft -> BeanCopyUtils.toType(ft, ListFileTaskVo.class));
    }
}
