package com.yongj.remote;

import com.yongj.file.remote.vo.GenFileTempTokenReq;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * @author yongj.zhuang
 */
@Slf4j
@SpringBootTest
public class UserFileServiceFeignControllerTest {

    @Autowired
    private UserFileServiceFeignController ctrl;

    @Test
    public void should_gen_temp_tokens() {
        var keys = Arrays.asList("ZZZ473737645015040965849", "ZZZ473734540804096965849");
        var req = new GenFileTempTokenReq();
        req.setFileKeys(keys);

        var resp = ctrl.generateFileTempToken(req);
        var m = resp.getData();
        Assertions.assertNotNull(m);
        Assertions.assertTrue(m.size() > 0);
        for (String k : keys) {
            Assertions.assertTrue(m.containsKey(k));
            Assertions.assertTrue(StringUtils.hasText(m.get(k)));
        }
        log.info("m: {}", m);
    }

}
