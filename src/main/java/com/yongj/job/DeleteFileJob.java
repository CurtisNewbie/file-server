package com.yongj.job;

import com.curtisnewbie.common.vo.PagingVo;
import com.github.pagehelper.PageInfo;
import com.yongj.dao.FsGroup;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.services.FileInfoService;
import com.yongj.services.FsGroupService;
import com.yongj.vo.PhysicDeleteFileVo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

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

    /**
     * Physically delete files in every 2 hours
     */
    private static final String CRON_EXPRESSION = "0 0 0/2 ? * *";

    @Autowired
    private FileInfoService fileInfoService;
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
        PageInfo<PhysicDeleteFileVo> idsInPage = fileInfoService.findPagedFileIdsForPhysicalDeleting(paging);
        // while there is items in page
        while (!idsInPage.getList().isEmpty()) {
            log.info("Found {} files, preparing to delete them", idsInPage.getList().size());
            // delete the file physically
            deleteFilesPhysically(idsInPage);
            // next page
            idsInPage = fileInfoService.findPagedFileIdsForPhysicalDeleting(paging);
            paging.setPage(paging.getPage() + 1);
        }
        log.info("Physical file deleting job finished...");
    }

    // files that are unable to deleted, won't cause a transaction roll back
    private void deleteFilesPhysically(PageInfo<PhysicDeleteFileVo> pageInfo) {
        for (PhysicDeleteFileVo v : pageInfo.getList()) {
            // get the fs_group's folder
            FsGroup fsg = fsGroupService.findFsGroupById(v.getFsGroupId());
            Objects.requireNonNull(fsg, "fs_group not found, unable to delete files");

            // resolve absolute path
            String absPath = pathResolver.resolveAbsolutePath(v.getUuid(), v.getUploaderId(), fsg.getBaseFolder());
            try {
                // commit the actual deleting operation
                ioHandler.deleteFile(absPath);
                // mark as deleted
                fileInfoService.markFileDeletedPhysically(v.getId());
            } catch (IOException e) {
                log.warn("Unable to delete file, uuid: " + String.valueOf(v.getUuid()) + ", please try again later", e);
            }
        }
    }

}
