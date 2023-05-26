package com.yongj.job;

import com.curtisnewbie.common.data.LongWrapper;
import com.curtisnewbie.common.util.Paginator;
import com.curtisnewbie.common.util.Runner;
import com.curtisnewbie.module.task.annotation.JobDeclaration;
import com.curtisnewbie.module.task.scheduling.AbstractJob;
import com.curtisnewbie.module.task.vo.TaskVo;
import com.yongj.dao.FileInfo;
import com.yongj.dao.FsGroup;
import com.yongj.services.FileService;
import com.yongj.services.FsGroupService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Job for scanning phantom files
 *
 * @author yongj.zhuang
 */
@Slf4j
@Component
@JobDeclaration(name = "Job that scans phantom files", cron = "0 0 0/1 ? * *")
public class ScanPhantomFileJob extends AbstractJob {

    @Autowired
    private FsGroupService fsGroupService;
    @Autowired
    private FileService fileService;

    @Override
    protected void executeInternal(TaskVo task) throws JobExecutionException {
        log.info("ScanPhantomFileJob start");

        Paginator<FsGroup> paginator = new Paginator<FsGroup>()
                .isTimed(true)
                .nextPageSupplier(p -> fsGroupService.listFsGroups(p));

        final LongWrapper count = new LongWrapper(0);
        paginator.loopEachTilEnd(fsGroup -> {
            final String baseFolder = fsGroup.getBaseFolder();
            final File file = new File(baseFolder);
            if (!file.exists()) {
                log.warn("FsGroup base folder not exists: '{}'", baseFolder);
                return;
            }

            Runner.tryRun(() -> {
                Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String name = file.toFile().getName();
                        final FileInfo f = fileService.findByKey(name);
                        if (f == null) log.warn("File {} isn't in database", file.toString());

                        count.incr();
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                        return FileVisitResult.CONTINUE;
                    }
                });
            });
        });

        task.setLastRunResult(String.format("Finished, visited %s files", count.getValue()));
        log.info("ScanPhantomFileJob  end, time: {}", paginator.getTimer().printDuration());
    }
}
