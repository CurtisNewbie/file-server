package com.yongj.services;

import com.curtisnewbie.common.util.MultipartUtil;
import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.dao.FileInfo;
import com.yongj.dao.FileInfoMapper;
import com.yongj.dao.FsGroup;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.enums.FsGroupMode;
import com.yongj.vo.ListFileInfoReqVo;
import com.yongj.vo.UpdateFileCmd;
import com.yongj.vo.UploadFileVo;
import com.yongj.vo.UploadZipFileVo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

//todo Fix the test cases, these doesn't work for FsGroup, basePath is deprecated already

/**
 * Test for {@link FileService}
 *
 * @author yongjie.zhuang
 */
@Slf4j
@SpringBootTest
@Transactional
public class FileServiceTest {

    private static final String TEST_DOWNLOADED_FILE = "downloaded-file";
    private static final String UPLOADED_TEST_FILE_ZIP = "test-files.zip";
    private static final String UPLOADED_TEST_FILE = "test-file.txt";
    private static final String UPLOADED_TEST_FILE_2 = "test-file2.txt";
    private static final int TEST_USER_ID = 1;
    private static final int TEST_USER_ID_2 = 2;

    @Autowired
    FileService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Value("${base.path}")
    String basePath;

    @MockBean
    FsGroupService fsGroupService;


    /** Test {@link FileService#updateFile(UpdateFileCmd)} */
    @Test
    void shouldUploadFile() {
        mockFsGroupService();
        Assertions.assertDoesNotThrow(() -> {
            FileInfo fi = uploadTestFile(TEST_USER_ID, UPLOADED_TEST_FILE);
            Assertions.assertNotNull(fi, "No FileInfo returned");
            Path fp = Paths.get(
                    basePath.concat(File.separator)
                            .concat(fi.getUploaderId().toString())
                            .concat(File.separator)
                            .concat(fi.getUuid()));
            Assertions.assertTrue(Files.exists(fp), "Uploaded file doesn't exist");

            // cleanup test data
            doCleanUp(fp);
        });
    }

    /** Test {@link FileService#uploadFilesAsZip(UploadZipFileVo)} */
    @Test
    public void shouldUploadFilesAsZip() throws IOException {
        mockFsGroupService();

        MultipartFile mf = MultipartUtil.toMultipartFile(getTestFile(UPLOADED_TEST_FILE), UPLOADED_TEST_FILE);
        MultipartFile mf1 = MultipartUtil.toMultipartFile(getTestFile(UPLOADED_TEST_FILE_2), UPLOADED_TEST_FILE_2);

        Assertions.assertDoesNotThrow(() -> {
            FileInfo fi = fileInfoService.uploadFilesAsZip(UploadZipFileVo.builder()
                    .userId(TEST_USER_ID)
                    .username("zhuangyongj")
                    .zipFile(UPLOADED_TEST_FILE_ZIP)
                    .userGroup(FileUserGroupEnum.PRIVATE)
                    .multipartFiles(new MultipartFile[]{mf, mf1})
                    .build()).get();
            Assertions.assertNotNull(fi, "No FileInfo returned");
            Path fp = Paths.get(
                    basePath.concat(File.separator)
                            .concat(fi.getUploaderId().toString())
                            .concat(File.separator)
                            .concat(fi.getUuid()));
            Assertions.assertTrue(Files.exists(fp), "Uploaded file doesn't exist");
            // look at the zip's internal
            ZipFile zf = new ZipFile(fp.toFile());
            Assertions.assertEquals(zf.size(), 2);

            // cleanup test data
            doCleanUp(fp);
        });
    }

    private InputStream getTestFile(String testFile) {
        return this.getClass().getClassLoader().getResourceAsStream(testFile);
    }


    /** Test {@link FileService#findPagedFilesForUser(ListFileInfoReqVo)} */
    @Test
    void shouldFindPagedFilesForUser() {
        ListFileInfoReqVo reqVo = new ListFileInfoReqVo();
        PagingVo pv = new PagingVo();
        pv.setLimit(10);
        pv.setPage(1);
        reqVo.setPagingVo(pv);
        Assertions.assertNotNull(fileInfoService.findPagedFilesForUser(reqVo));
    }

    /** Test {@link FileService#findPagedFileIdsForPhysicalDeleting(PagingVo)} */
    @Test
    void shouldFindPagedFilesForPhysicalDeleting() {
        PagingVo pv = new PagingVo();
        pv.setLimit(10);
        pv.setPage(1);
        Assertions.assertNotNull(fileInfoService.findPagedFileIdsForPhysicalDeleting(pv));
    }

    private void doCleanUp(Path fp) {
        // cleanup test data
        try {
            Files.delete(fp);
        } catch (IOException e) {
            log.error("Unable to delete generated test file", e);
        }
    }

    private void doCleanUp(FileInfo fi) {
        Path fp = Paths.get(
                basePath.concat(File.separator)
                        .concat(fi.getUploaderId().toString())
                        .concat(File.separator)
                        .concat(fi.getUuid()));
        doCleanUp(fp);
    }

    @Test
    void shouldRetrieveFileInputStream() throws IOException {
        mockFsGroupService();
        FileInfo fi = uploadTestFile(TEST_USER_ID, UPLOADED_TEST_FILE);
        Assertions.assertNotNull(fi);
        Assertions.assertNotNull(fileInfoService.retrieveFileInputStream(fi.getId()));
        doCleanUp(fi);
    }

    @Test
    void shouldValidateUserDownload() throws IOException {
        mockFsGroupService();
        FileInfo fi = uploadTestFile(TEST_USER_ID, UPLOADED_TEST_FILE);
        Assertions.assertNotNull(fi);

        Assertions.assertThrows(Exception.class, () -> {
            fileInfoService.validateUserDownload(TEST_USER_ID_2, fi.getId());
        });

        doCleanUp(fi);
    }

    @Test
    @Rollback
    void shouldDeleteFileLogically() throws IOException {
        mockFsGroupService();
        FileInfo fi = uploadTestFile(TEST_USER_ID, UPLOADED_TEST_FILE);
        Assertions.assertNotNull(fi);

        Assertions.assertDoesNotThrow(() -> {
            fileInfoService.deleteFileLogically(TEST_USER_ID, fi.getId());
            FileInfo sfi = fileInfoMapper.selectById(fi.getId());
            Assertions.assertNotNull(sfi);
            Assertions.assertEquals(sfi.getIsLogicDeleted(), FileLogicDeletedEnum.LOGICALLY_DELETED.getValue());
        });

        doCleanUp(fi);
    }

    /** Test {@link FileService#markFileDeletedPhysically(int)} */
    @Test
    @Rollback
    void shouldMarkFileDeletedPhysically() throws IOException {
        mockFsGroupService();
        FileInfo fi = uploadTestFile(TEST_USER_ID, UPLOADED_TEST_FILE);
        Assertions.assertNotNull(fi);

        Assertions.assertDoesNotThrow(() -> {
            fileInfoService.markFileDeletedPhysically(fi.getId());
            FileInfo sfi = fileInfoMapper.selectById(fi.getId());
            Assertions.assertNotNull(sfi);
            Assertions.assertEquals(sfi.getIsPhysicDeleted(), FilePhysicDeletedEnum.PHYSICALLY_DELETED.getValue());
        });

        doCleanUp(fi);
    }

    private void mockFsGroupService() {
        Mockito.when(fsGroupService.findFirstFsGroupForWrite()).then((ivk) -> {
            FsGroup fsg = new FsGroup();
            fsg.setId(1);
            fsg.setBaseFolder(basePath);
            fsg.setMode(FsGroupMode.READ_WRITE.getValue());
            fsg.setName("default");
            return fsg;
        });

        Mockito.when(fsGroupService.findFsGroupById(Mockito.eq(1))).then((ivk) -> {
            FsGroup fsg = new FsGroup();
            fsg.setId(1);
            fsg.setBaseFolder(basePath);
            fsg.setMode(FsGroupMode.READ_WRITE.getValue());
            fsg.setName("default");
            return fsg;
        });
    }

    @SneakyThrows
    private FileInfo uploadTestFile(int userId, String fileName) {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");

        return fileInfoService.uploadFile(UploadFileVo.builder()
                .userId(userId)
                .fileName(fileName)
                .username("zhuangyongj")
                .userGroup(FileUserGroupEnum.PRIVATE)
                .inputStream(inputStream)
                .build()).get();
    }
}
