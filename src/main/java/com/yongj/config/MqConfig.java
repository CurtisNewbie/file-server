package com.yongj.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * @author yongjie.zhuang
 */
@Configuration
@ImportResource("classpath:rabbitmq.xml")
public class MqConfig {
}
