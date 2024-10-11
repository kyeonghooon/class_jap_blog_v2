package com.tenco.blog_v1.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorConfig {

    @Bean // 빈으로 등록 처리 : 로그인 인터셉터를 빈으로 등록
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean //
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }
    //

    //

    //

}
