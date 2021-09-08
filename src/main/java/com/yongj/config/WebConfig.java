package com.yongj.config;

import com.curtisnewbie.common.converters.EpochDateLongConverter;
import com.curtisnewbie.common.converters.EpochDateStringConverter;
import com.curtisnewbie.common.converters.EpochLongDateConverter;
import com.curtisnewbie.common.converters.EpochStringDateConverter;
import com.curtisnewbie.module.tracing.filter.FieldNameBasedExtractor;
import com.curtisnewbie.module.tracing.filter.TracingHandlerInterceptor;
import com.curtisnewbie.service.auth.remote.vo.UserVo;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

/**
 * Configuration of Web MVC
 *
 * @author yongjie.zhuang
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final int RESOURCES_CACHE_MAX_AGE_HOURS = 24;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // cache 10 minutes for the static resources
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(CacheControl.maxAge(RESOURCES_CACHE_MAX_AGE_HOURS, TimeUnit.HOURS));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        try {
            registry.addInterceptor(
                    new TracingHandlerInterceptor(
                            new FieldNameBasedExtractor("username", UserVo.class)
                    )
            );
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EpochDateLongConverter());
        registry.addConverter(new EpochLongDateConverter());
        registry.addConverter(new EpochDateStringConverter());
        registry.addConverter(new EpochStringDateConverter());
    }

}
