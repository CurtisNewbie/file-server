package com.yongj.job;

import com.curtisnewbie.common.vo.PageableList;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.dao.FsGroup;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.services.FileService;
import com.yongj.services.FsGroupService;
import com.yongj.vo.PhysicDeleteFileVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.curtisnewbie.common.util.ExceptionUtils.throwIllegalState;

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
public class DeleteFileJob implements Job {

    private static final Integer LIMIT = 100;

    @Autowired
    private FileService fileInfoService;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private FsGroupService fsGroupService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Physical file deleting job started...");
        PagingVo paging = new PagingVo();
        paging.setLimit(LIMIT);
        paging.setPage(1);

        // first page
        PageableList<PhysicDeleteFileVo> idsInPage = fileInfoService.findPagedFileIdsForPhysicalDeleting(paging);
        // while there are items in page
        while (!idsInPage.getPayload().isEmpty()) {

            log.info("Found {} files, preparing to delete them", idsInPage.getPayload().size());

            // delete the file physically
            deleteFilesPhysically(idsInPage.getPayload());

            // next page
            paging.setPage(paging.getPage() + 1);
            idsInPage = fileInfoService.findPagedFileIdsForPhysicalDeleting(paging);
        }
        log.info("Physical file deleting job finished...");
    }

    // files that are unable to delete, won't cause a transaction roll back, we just print an error log
    private void deleteFilesPhysically(List<PhysicDeleteFileVo> list) {
        for (PhysicDeleteFileVo v : list) {

            // get the fs_group's folder
            final int fsgId = v.getFsGroupId();
            final FsGroup fsg = fsGroupService.findFsGroupById(fsgId);
            if (fsg == null || fsg.isDeleted())
                throwIllegalState("fs_group: %s not found or is deleted, unable to delete files", fsgId);

            // resolve absolute path
            final String absPath = pathResolver.resolveAbsolutePath(v.getUuid(), v.getUploaderId(), fsg.getBaseFolder());
            try {
                // commit the actual deleting operation
                ioHandler.deleteFile(absPath);
                // mark as deleted
                fileInfoService.markFileDeletedPhysically(v.getId());
            } catch (IOException e) {
                log.error("Unable to delete file, uuid: " + String.valueOf(v.getUuid()) + ", please try again later", e);
            }
        }
    }

}
