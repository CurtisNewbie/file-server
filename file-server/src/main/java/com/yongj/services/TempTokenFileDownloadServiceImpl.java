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

    private final TokenGenerator tokenGenerator = new NumberTokenGenerator();

    @Autowired
    private RedisController redisController;

    @Override
    public String generateTempTokenForFile(int id, int minutes) throws MsgEmbeddedException {
        final String token = tokenGenerator.generate(Optional.of(15));
        log.info("Generated token: {} for file's id: {}", token, id);

        if (!redisController.expire(token, id, minutes,TimeUnit.MINUTES)) {
            throw new MsgEmbeddedException("Unable to generate token, please try again later");
        }
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
