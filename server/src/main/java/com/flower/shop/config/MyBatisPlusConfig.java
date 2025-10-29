package com.flower.shop.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.flower.shop.mapper")
public class MyBatisPlusConfig {
}