package com.yongj.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author yongjie.zhuang
 */
@Configuration
public class FeignInterceptor {

    @Value("${jwt-token}")
    private String token;

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor() {
        return new TokenRequestInterceptor(token);
    }

    // todo make the jwt token temporary
    public static class TokenRequestInterceptor implements RequestInterceptor {

        private final String token;

        public TokenRequestInterceptor(String token) {
            this.token = token;
        }

        @Override
        public void apply(RequestTemplate requestTemplate) {
            requestTemplate.header("auth-token", token);

        }
    }

}
