package com.zyq.springbootthymeleaf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置图片资源映射
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:E:/桌面/javaEE/仿注册/备份/SpringBoot-Thymeleaf（版本二） - 副本/uploads/images/");

        // 配置文件资源映射
        registry.addResourceHandler("/uploads/files/**")
                .addResourceLocations("file:E:/桌面/javaEE/仿注册/备份/SpringBoot-Thymeleaf（版本二） - 副本/uploads/files/");

        // 新增作品集图片映射
        registry.addResourceHandler("/uploads/portfolio/**")
                .addResourceLocations("file:E:/桌面/javaEE/仿注册/备份/SpringBoot-Thymeleaf（版本二） - 副本/uploads/portfolio/");
    }

}