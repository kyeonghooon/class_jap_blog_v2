package com.tenco.blog_v1.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Component  // IOC
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired // DI 처리
    private LoginInterceptor loginInterceptor;
    @Autowired
    private AdminInterceptor adminInterceptor;

    /**
     * 인터셉터를 등록하고 적용할 URL 패턴을 설정하는 메서드이다.
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")  // 인터셉터를 적용할 경로 패턴 설정
                .excludePathPatterns("/public/**", "/login-form", "/login", "/join-form", "/join", "/"); // 인터셉터를 제외할 경로 패턴 설정
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
