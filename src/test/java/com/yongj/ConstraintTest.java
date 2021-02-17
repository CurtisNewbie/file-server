package com.yongj;

import com.yongj.io.api.FileManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.validation.ConstraintViolationException;

/**
 * @author yongjie.zhuang
 */
@SpringBootTest
public class ConstraintTest {

    private static final Logger logger = LoggerFactory.getLogger(ConstraintTest.class);

    @Autowired
    private FileManager fileManager;

    @Test
    void testAnnotationConstraint() {
        logger.info("Start testing constraints validation");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Assertions.assertThrows(ConstraintViolationException.class, () -> {
            fileManager.cache(null);
        });
        stopWatch.stop();
        logger.info("Done testing constraints validation, took: {} millisec", stopWatch.getTotalTimeMillis());
    }
}
