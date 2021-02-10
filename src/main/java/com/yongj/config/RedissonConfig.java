package com.yongj.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author yongjie.zhuang
 */
@Configuration
public class RedissonConfig {
    private static final Logger logger = LoggerFactory.getLogger(RedissonConfig.class);

    @Value("${redis.address}")
    private String redisAdd;

    @Value("${redis.port}")
    private int redisPort;

    @PostConstruct
    void init() {
        logger.info("[INIT] Configuring Redisson for {}:{}", redisAdd, redisPort);
    }

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();
        config.useSingleServer()
                .setAddress(String.format("redis://%s:%d", redisAdd, redisPort));
        return Redisson.create(config);
    }
}