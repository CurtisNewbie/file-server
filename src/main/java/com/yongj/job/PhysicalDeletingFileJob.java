package com.yongj.job;

import com.github.pagehelper.PageInfo;
import com.yongj.io.IOHandler;
import com.yongj.io.PathResolver;
import com.yongj.services.FileInfoService;
import com.yongj.vo.PagingVo;
import com.yongj.vo.PhysicDeleteFileVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;

/**
 * Job for physically deleting the files
 * <p>
 * Files are marked as either normal, logically deleted, or physically deleted. This job is responsible for deleting the
 * files that are logically deleted but not physically deleted.
 * </p>
 *
 * @author yongjie.zhuang
 */
@Component
public class PhysicalDeletingFileJob implements ScheduledJob {

    private static final Integer LIMIT = 100;
    private static final Logger logger = LoggerFactory.getLogger(PhysicDeleteFileVo.class);
    private static final MessageFormat UNABLE_TO_DELETE_FILE_FORMAT = new MessageFormat("Unable to delete file" +
            ", uuid: {0}, please make sure that it's actually there, and try again later");

    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private IOHandler ioHandler;
    @Autowired
    private PathResolver pathResolver;

    /**
     * Physically delete files in every 2 hours
     */
    @Scheduled(cron = "0 0 0/2 ? * *")
    @Override
    public void _exec() {
        logger.info("Physical file deleting job started");
        PagingVo paging = new PagingVo();
        paging.setLimit(LIMIT);
        paging.setPage(1);

        // first page
        PageInfo<PhysicDeleteFileVo> idsInPage = fileInfoService.findPagedFileIdsForPhysicalDeleting(paging);
        // while there is items in page
        while (!idsInPage.getList().isEmpty()) {
            // delete the file physically
            deleteFilesPhysically(idsInPage);
            // next page
            idsInPage = fileInfoService.findPagedFileIdsForPhysicalDeleting(paging);
            paging.setPage(paging.getPage() + 1);
        }
        logger.info("Physical file deleting job finished");
    }

    // files that are unable to deleted, won't cause a transaction roll back
    private void deleteFilesPhysically(PageInfo<PhysicDeleteFileVo> pageInfo) {
        for (PhysicDeleteFileVo v : pageInfo.getList()) {
            // resolve absolute path
            String absPath = pathResolver.resolveAbsolutePath(v.getUuid(), v.getUploaderId());
            try {
                // commit the actual deleting operation
                ioHandler.deleteFile(absPath);
                // mark as deleted
                fileInfoService.markFileDeletedPhysically(v.getId());
            } catch (IOException e) {
                logger.warn(UNABLE_TO_DELETE_FILE_FORMAT.format(v.getUuid()), e);
            }
        }
    }

}
