package com.yongj.services;

import com.curtisnewbie.common.vo.PagingVo;
import com.yongj.dao.FileInfo;
import com.yongj.dao.FileInfoMapper;
import com.yongj.enums.FileLogicDeletedEnum;
import com.yongj.enums.FilePhysicDeletedEnum;
import com.yongj.enums.FileUserGroupEnum;
import com.yongj.vo.ListFileInfoReqVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipFile;

/**
 * Test for {@link FileInfoService}
 *
 * @author yongjie.zhuang
 */
@Slf4j
@SpringBootTest
@Rollback
public class FileInfoServiceTest {

    private static final String TEST_DOWNLOADED_FILE = "downloaded-file";
    private static final String UPLOADED_TEST_FILE_ZIP = "test-files.zip";
    private static final String UPLOADED_TEST_FILE = "test-file.txt";
    private static final String UPLOADED_TEST_FILE_2 = "test-file2.txt";
    private static final int TEST_USER_ID = 1;
    private static final int TEST_USER_ID_2 = 2;

    @Autowired
    FileInfoService fileInfoService;

    @Autowired
    FileInfoMapper fileInfoMapper;

    @Value("${base.path}")
    String basePath;

    /** Test {@link FileInfoService#uploadFile(int, String, FileUserGroupEnum, InputStream)} */
    @Test
    void shouldUploadFile() {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
        Assertions.assertDoesNotThrow(() -> {
            FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
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

    /** Test {@link FileInfoService#uploadFilesAsZip(int, String, String[], FileUserGroupEnum, InputStream[])} */
    @Test
    public void shouldUploadFilesAsZip() {

        String[] entryNames = new String[2];
        InputStream[] iss = new InputStream[2];

        InputStream tf1 = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(tf1);
        entryNames[0] = UPLOADED_TEST_FILE;
        iss[0] = tf1;

        InputStream tf2 = getTestFile(UPLOADED_TEST_FILE_2);
        Assertions.assertNotNull(tf2);
        entryNames[1] = UPLOADED_TEST_FILE_2;
        iss[1] = tf2;

        Assertions.assertDoesNotThrow(() -> {
            FileInfo fi = fileInfoService.uploadFilesAsZip(TEST_USER_ID, UPLOADED_TEST_FILE_ZIP, entryNames,
                    FileUserGroupEnum.PRIVATE, iss);
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


    /** Test {@link FileInfoService#findFilesForUser(int)} */
    @Test
    void shouldFindFilesForUser() {
        Assertions.assertNotNull(fileInfoService.findFilesForUser(TEST_USER_ID));
    }

    /** Test {@link FileInfoService#findPagedFilesForUser(ListFileInfoReqVo)} */
    @Test
    void shouldFindPagedFilesForUser() {
        ListFileInfoReqVo reqVo = new ListFileInfoReqVo();
        PagingVo pv = new PagingVo();
        pv.setLimit(10);
        pv.setPage(1);
        reqVo.setPagingVo(pv);
        Assertions.assertNotNull(fileInfoService.findPagedFilesForUser(reqVo));
    }

    /** Test {@link FileInfoService#findPagedFileIdsForPhysicalDeleting(PagingVo)} */
    @Test
    void shouldFindPagedFilesForPhysicalDeleting() {
        PagingVo pv = new PagingVo();
        pv.setLimit(10);
        pv.setPage(1);
        Assertions.assertNotNull(fileInfoService.findPagedFileIdsForPhysicalDeleting(pv));
    }

    /** Test {@link FileInfoService#downloadFile(String, OutputStream)} */
    @Test
    void shouldDownloadFile() throws IOException {
        Path dp = Paths.get(basePath.concat(File.separator).concat(TEST_DOWNLOADED_FILE));
        try (
                OutputStream fout = new FileOutputStream(dp.toFile());
        ) {
            final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
            Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
            FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
            Assertions.assertNotNull(fi);

            fileInfoService.downloadFile(fi.getUuid(), fout);
            Assertions.assertTrue(Files.exists(dp));

            doCleanUp(fi);
            doCleanUp(dp);
        }
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

    /** Test {@link FileInfoService#getFilename(String)} */
    @Test
    void shouldGetFilename() throws IOException {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
        FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
        Assertions.assertNotNull(fi);

        String fname = fileInfoService.getFilename(fi.getUuid());
        Assertions.assertEquals(fname, UPLOADED_TEST_FILE);
        doCleanUp(fi);
    }


    /** Test {@link FileInfoService#retrieveFileInputStream(String)} */
    @Test
    void ShouldRetrieveFileInputStream() throws IOException {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
        FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
        Assertions.assertNotNull(fi);
        Assertions.assertNotNull(fileInfoService.retrieveFileInputStream(fi.getUuid()));
        doCleanUp(fi);
    }

    @Test
    void shouldValidateUserDownload() throws IOException {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
        FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
        Assertions.assertNotNull(fi);

        Assertions.assertThrows(Exception.class, () -> {
            fileInfoService.validateUserDownload(TEST_USER_ID_2, fi.getUuid());
        });

        doCleanUp(fi);
    }

    @Test
    void shouldDeleteFileLogically() throws IOException {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
        FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
        Assertions.assertNotNull(fi);

        Assertions.assertDoesNotThrow(() -> {
            fileInfoService.deleteFileLogically(TEST_USER_ID, fi.getUuid());
            FileInfo sfi = fileInfoMapper.selectByPrimaryKey(fi.getId());
            Assertions.assertNotNull(sfi);
            Assertions.assertEquals(sfi.getIsLogicDeleted(), FileLogicDeletedEnum.LOGICALLY_DELETED.getValue());
        });

        doCleanUp(fi);
    }

    @Test
    void shouldMarkFileDeletedPhysically() throws IOException {
        final InputStream inputStream = getTestFile(UPLOADED_TEST_FILE);
        Assertions.assertNotNull(inputStream, "Unable to find the file that will be uploaded");
        FileInfo fi = fileInfoService.uploadFile(TEST_USER_ID, UPLOADED_TEST_FILE, FileUserGroupEnum.PRIVATE, inputStream);
        Assertions.assertNotNull(fi);

        Assertions.assertDoesNotThrow(() -> {
            fileInfoService.markFileDeletedPhysically(fi.getId());
            FileInfo sfi = fileInfoMapper.selectByPrimaryKey(fi.getId());
            Assertions.assertNotNull(sfi);
            Assertions.assertEquals(sfi.getIsPhysicDeleted(), FilePhysicDeletedEnum.PHYSICALLY_DELETED.getValue());
        });

        doCleanUp(fi);
    }
}
