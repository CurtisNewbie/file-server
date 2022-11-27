package com.yongj.config;

import com.curtisnewbie.common.formatters.DateEpochFormatter;
import com.curtisnewbie.common.formatters.LocalDateTimeEpochFormatter;
import com.curtisnewbie.common.util.AsyncUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.format.FormatterRegistry;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Callable;

/**
 * @author yongj.zhuang
 */
@Slf4j
@EnableAsync
@Configuration
public class MvcAsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "taskExecutor")
    public AsyncTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(AsyncUtils.AVAILABLE_PROCESSORS * 2);
        executor.setMaxPoolSize(AsyncUtils.AVAILABLE_PROCESSORS * 2);
        executor.setQueueCapacity(500);
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /** Configure async support for Spring MVC. */
    @Bean
    public WebMvcConfigurer webMvcConfigurer(AsyncTaskExecutor taskExecutor, CallableProcessingInterceptor callableProcessingInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setDefaultTimeout(360_000).setTaskExecutor(taskExecutor);
                configurer.registerCallableInterceptors(callableProcessingInterceptor);
                WebMvcConfigurer.super.configureAsyncSupport(configurer);
            }

            @Override
            public void addFormatters(FormatterRegistry registry) {
                registry.addFormatter(new DateEpochFormatter());
                registry.addFormatter(new LocalDateTimeEpochFormatter());
            }
        };
    }

    @Bean
    public CallableProcessingInterceptor callableProcessingInterceptor() {
        return new TimeoutCallableProcessingInterceptor() {
            @Override
            public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
                log.error("Async request timeout, request: {}", ((ServletWebRequest) request.getNativeRequest()).getRequest().getRequestURI());
                return new AsyncRequestTimeoutException();
            }
        };
    }
}
