package com.zyq.springbootthymeleaf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将/upload/images/** 映射到文件系统中的实际图片目录
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:E:/桌面/javaEE/仿注册/备份/SpringBoot-Thymeleaf（版本二）/uploads/images/");
    }
}