package com.curtisnewbie.file.remote;

import com.curtisnewbie.common.vo.Result;
import com.curtisnewbie.module.auth.config.EnableFeignJwtAuthorization;
import com.yongj.file.remote.FileServiceFeign;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.io.IOException;
import java.io.InputStream;

import static com.curtisnewbie.common.util.MultipartUtil.toMultipartFile;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@SpringBootApplication
@EnableFeignJwtAuthorization
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yongj.file.remote")
@SpringBootTest
public class FileServiceFeignTest {

    @Autowired
    private FileServiceFeign fileServiceFeign;

    @Test
    public void should_upload_file_via_feign() throws IOException {
        Result<String> result = fileServiceFeign.uploadAppFile("test-file",
                toMultipartFile(getTestFile("test-file.txt"), "test-file"),
                "some-server"
        );

        log.info("Result: {}", result);
        Assertions.assertTrue(result.isOk());
        Assertions.assertNotNull(result.getData());
    }

    private InputStream getTestFile(String testFile) {
        return this.getClass().getClassLoader().getResourceAsStream(testFile);
    }
}
