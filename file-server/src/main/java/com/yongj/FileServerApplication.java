package com.yongj;

import com.curtisnewbie.common.advice.EnableRoleControl;
import com.curtisnewbie.common.dao.EnableMBTraceInterceptor;
import com.curtisnewbie.service.auth.messaging.helper.EnableOperateLog;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableMBTraceInterceptor
@EnableRoleControl
@EnableOperateLog
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.curtisnewbie.service.auth.remote.feign")
@SpringBootApplication(scanBasePackages = "com.curtisnewbie")
@MapperScan("com.yongj.dao")
public class FileServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileServerApplication.class, args);
    }
}
