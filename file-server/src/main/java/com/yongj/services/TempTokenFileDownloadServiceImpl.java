package com.yongj.services;

import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.util.NumberTokenGenerator;
import com.yongj.util.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.concurrent.TimeUnit;

import static com.curtisnewbie.common.util.AssertUtils.isTrue;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class TempTokenFileDownloadServiceImpl implements TempTokenFileDownloadService {

    private static final int TOKEN_LEN = 15;
    private final TokenGenerator tokenGenerator = new NumberTokenGenerator();

    @Autowired
    private RedisController redisController;

    @Override
    public String generateTempTokenForFile(int id, int minutes) {
        final String token = tokenGenerator.generate(TOKEN_LEN);
        log.info("Generated token: {} for file's id: {}, exp: {} min", token, id, minutes);

        isTrue(redisController.setIfNotExists(token, id, minutes, TimeUnit.MINUTES),
                "Unable to generate token, please try again later");
        return token;
    }

    @Override
    public Integer getIdByToken(@NotEmpty String token) {
        return redisController.get(token);
    }

    @Override
    public void removeToken(@NotEmpty String token) {
        redisController.delete(token);
    }

}
