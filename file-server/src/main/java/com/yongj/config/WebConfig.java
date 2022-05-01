package com.yongj.config;

import com.curtisnewbie.common.converters.EpochDateLongConverter;
import com.curtisnewbie.common.converters.EpochDateStringConverter;
import com.curtisnewbie.common.converters.EpochLongDateConverter;
import com.curtisnewbie.common.converters.EpochStringDateConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration of Web MVC
 *
 * @author yongjie.zhuang
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new EpochDateLongConverter());
        registry.addConverter(new EpochLongDateConverter());
        registry.addConverter(new EpochDateStringConverter());
        registry.addConverter(new EpochStringDateConverter());
    }

}
