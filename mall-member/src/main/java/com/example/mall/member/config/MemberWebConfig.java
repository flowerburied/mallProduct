package com.example.mall.member.config;

import com.example.mall.member.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

    @Resource
    LoginUserInterceptor loginUserInterceptor;

    //只有登录了才能访问
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
    }
}
