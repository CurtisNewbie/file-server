package com.yongj.remote.feign;

import com.curtisnewbie.common.vo.Result;
import com.yongj.remote.AppFileServiceFeignController;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

import static com.curtisnewbie.common.util.MultipartUtil.toMultipartFile;

/**
 * @author yongjie.zhuang
 */
@Slf4j
@Configuration
@SpringBootTest
public class AppFileServiceFeignTest {

    @Autowired
    private AppFileServiceFeignController appFileServiceFeign;

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

    private InputStream getTestFile(String testFile) {
        return this.getClass().getClassLoader().getResourceAsStream(testFile);
    }
}
