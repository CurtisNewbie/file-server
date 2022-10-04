package com.yongj.job;

import com.curtisnewbie.common.util.Paginator;
import com.curtisnewbie.module.task.scheduling.AbstractJob;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.yongj.dao.FsGroup;
import com.yongj.services.FsGroupService;
import com.yongj.util.IOUtils;
import com.yongj.vo.ScanFsGroupResult;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Job for scanning fs_group
 *
 * @author yongj.zhuang
 */
@Slf4j
@Component
public class ScanFsGroupSizeJob extends AbstractJob {

    @Autowired
    private FsGroupService fsGroupService;

    @Override
    protected void executeInternal(TaskVo task) throws JobExecutionException {
        log.info("ScanFsGroupSizeJob start");

        Paginator<FsGroup> paginator = new Paginator<FsGroup>()
                .isTimed(true)
                .nextPageSupplier(p -> fsGroupService.listFsGroups(p));

        paginator.loopEachTilEnd(fsGroup -> {
            final String baseFolder = fsGroup.getBaseFolder();
            final File file = new File(baseFolder);
            if (!file.exists()) {
                log.warn("FsGroup base folder not exists: '{}'", baseFolder);
                return;
            }

            final long size = IOUtils.sizeOfDir(file.toPath());
            log.info("FsGroup '{}' size: {}", baseFolder, size);
            fsGroupService.saveScanResult(ScanFsGroupResult.builder()
                    .id(fsGroup.getId())
                    .size(size)
                    .scanTime(LocalDateTime.now())
                    .build());
        });

        task.setLastRunResult(String.format("Finished, scanned %s FsGroups", paginator.getCount()));
        log.info("ScanFsGroupSizeJob end, time: {}", paginator.getTimer().printDuration());
    }
}
