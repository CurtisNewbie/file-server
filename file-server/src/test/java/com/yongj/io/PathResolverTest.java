package com.yongj.io;

import com.curtisnewbie.common.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author yongj.zhuang
 */
@SpringBootTest
public class PathResolverTest {

    @Autowired
    private PathResolver pathResolver;

    @Test
    public void should_validate_extension() {
        String fn = "Spring in Action, 5th Edition.pdf";
        Assertions.assertDoesNotThrow(() -> pathResolver.validateFileExtension(fn));

        String fn1 = "Spring in Action, 5th Edition.";
        Assertions.assertThrows(UnrecoverableException.class, () -> pathResolver.validateFileExtension(fn1));

        String fn2 = "Spring in Action, 5th Edition.abc";
        Assertions.assertThrows(UnrecoverableException.class, () -> pathResolver.validateFileExtension(fn2));

        String fn3 = "Spring in Action, 5th Edition";
        Assertions.assertThrows(UnrecoverableException.class, () -> pathResolver.validateFileExtension(fn3));
    }

}
