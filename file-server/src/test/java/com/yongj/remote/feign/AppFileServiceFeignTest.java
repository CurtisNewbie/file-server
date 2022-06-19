package com.yongj.remote.feign;

import com.curtisnewbie.common.vo.Result;
import com.yongj.file.remote.AppFileServiceFeign;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.curtisnewbie.common.util.MultipartUtil.toMultipartFile;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Configuration
@EnableFeignClients(basePackages = {"com.curtisnewbie.service.auth.remote.feign", "com.yongj.file.remote"})
@SpringBootTest
public class AppFileServiceFeignTest {

    @Autowired
    private AppFileServiceFeign appFileServiceFeign;

    @Test
    public void should_upload_file_via_feign() throws IOException {
        Result<String> result = appFileServiceFeign.uploadAppFile(
                toMultipartFile(getTestFile("test-file.txt"), "test-file"),
                "test-app",
                null
        );

        log.info("Result: {}", result);
        Assertions.assertTrue(result.isOk());
        Assertions.assertNotNull(result.getData());
    }

    @Test
    public void should_download_file_via_feign() throws IOException {
        String uuid = "e4e58252-4cf8-4bc5-ada2-e5dbd53421c4";
        final Response response = appFileServiceFeign.download(uuid);
        Assertions.assertNotNull(response);
        final InputStream in = response.body().asInputStream();

        final Path outPath = Paths.get("downloaded-test-file.txt");
        Files.copy(in, outPath);
        System.out.println("Downloaded: " + outPath.toString());
    }

    private InputStream getTestFile(String testFile) {
        return this.getClass().getClassLoader().getResourceAsStream(testFile);
    }
}
