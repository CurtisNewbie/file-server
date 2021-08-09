package com.yongj.services;

import com.curtisnewbie.common.exceptions.MsgEmbeddedException;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.util.NumberTokenGenerator;
import com.yongj.util.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class TempTokenFileDownloadServiceImpl implements TempTokenFileDownloadService {

    private static final long DEFAULT_TIME = 10;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MINUTES;
    private final TokenGenerator tokenGenerator = new NumberTokenGenerator();

    @Autowired
    private RedisController redisController;

    @Override
    public String generateTempTokenForFile(@NotEmpty String uuid) throws MsgEmbeddedException {
        final String token = tokenGenerator.generate(Optional.of(15));
        log.info("Generated token: {} for uuid: {}", token, uuid);

        if (!redisController.expire(token, uuid, DEFAULT_TIME, DEFAULT_TIME_UNIT)) {
            throw new MsgEmbeddedException("Unable to generate token, please try again later");
        }
        return token;
    }

    @Override
    public String getUuidByToken(@NotEmpty String token) {
        return redisController.get(token);
    }

    @Override
    public void removeToken(@NotEmpty String token) {
        redisController.delete(token);
    }

}
