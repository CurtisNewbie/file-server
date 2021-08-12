package com.yongj.web;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.DateUtils;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PagingVo;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.service.NodeCoordinationService;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.ListTaskByPageReqVo;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.curtisnewbie.module.task.vo.UpdateTaskReqVo;
import com.github.pagehelper.PageInfo;
import com.yongj.vo.ListTaskByPageReqFsVo;
import com.yongj.vo.ListTaskByPageRespFsVo;
import com.yongj.vo.TaskFsVo;
import com.yongj.vo.TriggerTaskReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yongjie.zhuang
 */
@RestController
@RequestMapping("${web.base-path}/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private NodeCoordinationService nodeCoordinationService;

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/list")
    public Result<ListTaskByPageRespFsVo> listTaskByPage(@RequestBody ListTaskByPageReqFsVo reqVo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(reqVo.getPagingVo());
        PageInfo<TaskVo> pi = taskService.listByPage(BeanCopyUtils.toType(reqVo, ListTaskByPageReqVo.class),
                reqVo.getPagingVo());
        ListTaskByPageRespFsVo resp = new ListTaskByPageRespFsVo();
        resp.setPagingVo(new PagingVo().ofTotal(pi.getTotal()));
        resp.setList(toTaskFsVoList(pi.getList()));
        return Result.of(resp);
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody UpdateTaskReqVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getId());

        if (vo.getCronExpr() != null && !JobUtils.isCronExprValid(vo.getCronExpr())) {
            return Result.error("Cron expression illegal");
        }
        if (vo.getEnabled() != null) {
            TaskEnabled tce = EnumUtils.parse(vo.getEnabled(), TaskEnabled.class);
            ValidUtils.requireNonNull(tce);
        }
        if (vo.getConcurrentEnabled() != null) {
            TaskConcurrentEnabled tce = EnumUtils.parse(vo.getConcurrentEnabled(), TaskConcurrentEnabled.class);
            ValidUtils.requireNonNull(tce);
        }

        taskService.updateById(vo);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("/trigger")
    public Result<Void> trigger(@RequestBody TriggerTaskReqVo vo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(vo.getId());
        TaskVo tv = taskService.selectById(vo.getId());
        nodeCoordinationService.coordinateJobTriggering(tv);
        return Result.ok();
    }

    private List<TaskFsVo> toTaskFsVoList(List<TaskVo> taskVo) {
        SimpleDateFormat sdf = DateUtils.getDefSimpleDateFormat();
        return taskVo.stream().map(tv -> {
            TaskFsVo tfv = BeanCopyUtils.toType(tv, TaskFsVo.class);
            tfv.setLastRunStartTime(sdf.format(tv.getLastRunStartTime()));
            tfv.setLastRunEndTime(sdf.format(tv.getLastRunEndTime()));
            return tfv;
        }).collect(Collectors.toList());
    }

}
