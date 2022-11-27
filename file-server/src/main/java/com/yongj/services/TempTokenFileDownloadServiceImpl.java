package com.yongj.services;

import com.curtisnewbie.common.exceptions.UnrecoverableException;
import com.curtisnewbie.common.util.AssertUtils;
import com.curtisnewbie.module.redisutil.RedisController;
import com.yongj.enums.TokenType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.curtisnewbie.common.util.AssertUtils.isTrue;

/**
 * @author yongjie.zhuang
 */
@Service
@Slf4j
public class TempTokenFileDownloadServiceImpl implements TempTokenFileDownloadService {

    @Autowired
    private RedisController redisController;

    @Override
    public void extendsStreamingTokenExp(String token, int minutes) {
        String payload = redisController.get(token);
        AssertUtils.notNull(payload, "Session expired, please try again");
        final String[] split = payload.split(":");
        if (split.length < 2 || TokenType.valueOf(split[1]) != TokenType.STREAMING) {
            throw new UnrecoverableException("Not permitted operation");
        }
        redisController.expire(token, minutes, TimeUnit.MINUTES);
    }

    @Override
    public String generateTempTokenForFile(int id, int minutes, TokenType tokenType) {
        final String token = UUID.randomUUID().toString(); // UUID is much harder to predict (subjectively)
        log.info("Generated token: {} for file's id: {}, exp: {} min", token, id, minutes);

        isTrue(redisController.setIfNotExists(token, id + ":" + tokenType.name(), minutes, TimeUnit.MINUTES),
                "Unable to generate token, please try again later");
        return token;
    }

    @Override
    public Integer getIdByToken(@NotEmpty String token) {
        String payload = redisController.get(token);
        if (payload == null) return null;
        return Integer.parseInt(payload.split(":")[0]);
    }

    @Override
    public void removeToken(@NotEmpty String token) {
        redisController.delete(token);
    }

}
