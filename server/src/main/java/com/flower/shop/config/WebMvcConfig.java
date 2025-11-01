package com.flower.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将 /uploads/** 映射到本地磁盘目录，便于前端通过返回的 URL 直接访问图片
        Path root = Paths.get("").toAbsolutePath();
        String fullPath = root.resolve(uploadDir).toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + fullPath + "/");
    }
}