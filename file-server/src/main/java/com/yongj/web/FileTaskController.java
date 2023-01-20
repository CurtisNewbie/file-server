package com.yongj.web;

import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.util.AsyncUtils;
import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.Result;
import com.yongj.services.qry.FileTaskQryService;
import com.yongj.vo.filetask.ListFileTaskReq;
import com.yongj.vo.filetask.ListFileTaskVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author yongj.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/file/task")
public class FileTaskController {

    @Autowired
    private FileTaskQryService fileTaskQryService;

    @PostMapping("/list")
    public DeferredResult<Result<PageableList<ListFileTaskVo>>> listFileTasks(@RequestBody ListFileTaskReq req) {
        return AsyncUtils.runAsyncResult(() -> fileTaskQryService.listFileTasks(TraceUtils.tUser().getUserNo(), req));
    }
}
