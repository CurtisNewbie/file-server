package com.yongj.job;

import com.curtisnewbie.module.task.scheduling.*;
import com.curtisnewbie.module.task.vo.*;
import com.yongj.dao.FsGroup;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.services.FileService;
import com.yongj.services.FsGroupService;
import com.yongj.vo.PhysicDeleteFileVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.curtisnewbie.common.util.ExceptionUtils.illegalState;

/**
 * Job for 'deleting' the files
 * <p>
 * Files are marked as either normal, logically deleted, or physically deleted. This job is responsible for deleting the
 * files that are logically deleted. Notice that the files may not be removed from the disk, it all depends on the
 * operation configured in IOHandler (see {@link com.yongj.io.operation.DeleteFileOperation}.
 * </p>
 *
 * @author yongjie.zhuang
 */
@Slf4j
@Component
public class DeleteFileJob extends AbstractJob {

    @Autowired
    private FileService fileInfoService;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private FsGroupService fsGroupService;

    @Override
    public void executeInternal(TaskVo task) throws JobExecutionException {
        log.info("Physical file deleting job started...");

        final List<PhysicDeleteFileVo> files = fileInfoService.findPagedFileIdsForPhysicalDeleting();
        log.info("Found {} files, preparing to delete them", files.size());

        // delete the file physically
        final long count = deleteFilesPhysically(files);
        task.setLastRunResult(String.format("Deleted %s files", count));

        log.info("Physical file deleting job finished...");
    }

    // files that are unable to delete, won't cause a transaction roll back, we just print an error log
    private long deleteFilesPhysically(List<PhysicDeleteFileVo> list) {
        long count = 0;
        for (PhysicDeleteFileVo v : list) {

            // if it's directory, just mark it as deleted
            if (v.isDir()) {
                // mark as deleted
                fileInfoService.markFileDeletedPhysically(v.getId());
                continue;
            }

            // get the fs_group's folder
            final int fsgId = v.getFsGroupId();
            final FsGroup fsg = fsGroupService.findFsGroupById(fsgId);
            if (fsg == null || fsg.isDeleted())
                throw illegalState("fs_group: %s not found or is deleted, unable to delete files", fsgId);

            // resolve absolute path
            final String absPath = pathResolver.resolveAbsolutePath(v.getUuid(), v.getUploaderId(), fsg.getBaseFolder());
            try {
                // commit the actual deleting operation
                ioHandler.deleteFile(absPath);
                // mark as deleted
                fileInfoService.markFileDeletedPhysically(v.getId());
                ++count;
            } catch (IOException e) {
                log.error("Unable to delete file, uuid: " + String.valueOf(v.getUuid()) + ", please try again later", e);
            }
        }
        return count;
    }

}
