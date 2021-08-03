package com.yongj;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@PropertySources({
        @PropertySource("classpath:dubbo.properties"),
        @PropertySource("classpath:application.properties"),
        @PropertySource("classpath:common.properties")
})
@EnableDubbo
@EnableGlobalMethodSecurity(prePostEnabled = true)
@SpringBootApplication
@EnableScheduling
@MapperScan("com.yongj.dao")
public class FileServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }

    @Configuration
    @ComponentScan("com.curtisnewbie")
    static class AuthModuleConfig {
    }

}
