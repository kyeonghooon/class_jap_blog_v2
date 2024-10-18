package com.tenco.blog_v1.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

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
        List<String> loginAddPath = new ArrayList<>();
        loginAddPath.add("/board/**");
        loginAddPath.add("/user/**");
        loginAddPath.add("/reply/**");
        List<String> loginExculdePath = new ArrayList<>();
        loginExculdePath.add("/board/{id:\\d+}");
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns(loginAddPath)  // 인터셉터를 적용할 경로 패턴 설정
                .excludePathPatterns(loginExculdePath); // 인터셉터를 제외할 경로 패턴 설정
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/admin/**");
    }
}
