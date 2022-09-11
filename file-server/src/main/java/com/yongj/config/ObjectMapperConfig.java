package com.yongj.config;

import com.curtisnewbie.common.util.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Config for {@link com.fasterxml.jackson.databind.ObjectMapper}
 *
 * @author yongjie.zhuang
 */
@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtils.constructsJsonMapper();
    }
}
