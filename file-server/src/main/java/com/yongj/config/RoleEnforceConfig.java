package com.yongj.config;

import com.curtisnewbie.common.advice.RoleEnforcedAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongj.zhuang
 */
@Configuration
public class RoleEnforceConfig {

    @Bean
    public RoleEnforcedAdvice roleEnforcedAdvice() {
        return new RoleEnforcedAdvice();
    }

}
