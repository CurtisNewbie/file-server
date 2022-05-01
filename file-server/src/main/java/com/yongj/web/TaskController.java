package com.yongj.web;

import com.curtisnewbie.common.advice.RoleRequired;
import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.common.trace.TraceUtils;
import com.curtisnewbie.common.util.BeanCopyUtils;
import com.curtisnewbie.common.util.EnumUtils;
import com.curtisnewbie.common.util.ValidUtils;
import com.curtisnewbie.common.vo.PageablePayloadSingleton;
import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.task.constants.TaskConcurrentEnabled;
import com.curtisnewbie.module.task.constants.TaskEnabled;
import com.curtisnewbie.module.task.scheduling.JobUtils;
import com.curtisnewbie.module.task.service.NodeCoordinationService;
import com.curtisnewbie.module.task.service.TaskHistoryService;
import com.curtisnewbie.module.task.service.TaskService;
import com.curtisnewbie.module.task.vo.*;
import com.curtisnewbie.service.auth.remote.exception.InvalidAuthenticationException;
import com.yongj.converters.TaskFsConverter;
import com.yongj.converters.TaskHistoryFsConverter;
import com.yongj.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private TaskHistoryService taskHistoryService;

    @Autowired
    private NodeCoordinationService nodeCoordinationService;

    @Autowired
    private TaskFsConverter taskFsConverter;

    @Autowired
    private TaskHistoryFsConverter taskHistoryFsConverter;

    @RoleRequired(role = "admin")
    @PostMapping("/list")
    public Result<ListTaskByPageRespFsVo> listTaskByPage(@RequestBody ListTaskByPageReqFsVo reqVo) throws MsgEmbeddedException {
        ValidUtils.requireNonNull(reqVo.getPagingVo());

        PageablePayloadSingleton<List<ListTaskByPageRespVo>> pi = taskService.listByPage(taskFsConverter.toListTaskByPageReqFsVo(reqVo),
                reqVo.getPagingVo());
        ListTaskByPageRespFsVo resp = new ListTaskByPageRespFsVo();
        resp.setPagingVo(pi.getPagingVo());
        resp.setList(
                pi.getPayload()
                        .stream()
                        .map(taskFsConverter::toFsVo)
                        .collect(Collectors.toList())
        );
        return Result.of(resp);
    }

    @RoleRequired(role = "admin")
    @PostMapping("/history")
    public Result<ListTaskHistoryByPageRespWebVo> listTaskHistoryByPage(@RequestBody ListTaskHistoryByPageReqWebVo reqVo)
            throws MsgEmbeddedException {
        ValidUtils.requireNonNull(reqVo.getPagingVo());

        PageablePayloadSingleton<List<ListTaskHistoryByPageRespVo>> pi = taskHistoryService.findByPage(BeanCopyUtils.toType(reqVo, ListTaskHistoryByPageReqVo.class));

        ListTaskHistoryByPageRespWebVo resp = new ListTaskHistoryByPageRespWebVo();
        resp.setList(
                pi.getPayload()
                        .stream()
                        .map(taskHistoryFsConverter::toWebVo)
                        .collect(Collectors.toList())
        );
        resp.setPagingVo(pi.getPagingVo());
        return Result.of(resp);
    }

    @RoleRequired(role = "admin")
    @PostMapping("/update")
    public Result<Void> update(@RequestBody UpdateTaskReqVo vo) throws MsgEmbeddedException, InvalidAuthenticationException {
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
        vo.setUpdateBy(TraceUtils.tUser().getUsername());
        taskService.updateById(vo);
        return Result.ok();
    }

    @RoleRequired(role = "admin")
    @PostMapping("/trigger")
    public Result<Void> trigger(@RequestBody TriggerTaskReqVo vo) throws MsgEmbeddedException, InvalidAuthenticationException {
        ValidUtils.requireNonNull(vo.getId());
        TaskVo tv = taskService.selectById(vo.getId());
        nodeCoordinationService.coordinateJobTriggering(tv, TraceUtils.tUser().getUsername());
        return Result.ok();
    }
}
