package com.yongj.services.qry;

import com.curtisnewbie.common.vo.PageableList;
import com.yongj.vo.filetask.ListFileTaskReq;
import com.yongj.vo.filetask.ListFileTaskVo;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Query Service for FileTask
 *
 * @author yongj.zhuang
 */
@Validated
public interface FileTaskQryService {

    PageableList<ListFileTaskVo> listFileTasks(@NotNull String userNo, @NotNull ListFileTaskReq req);

}
