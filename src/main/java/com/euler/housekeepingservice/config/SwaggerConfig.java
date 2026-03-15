package com.euler.housekeepingservice.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Knife4j) 接口文档全局配置
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("家政服务预约平台 API 文档")
                        .version("1.0")
                        .description("软件工程毕业设计核心接口"))
                // 1. 定义全局鉴权组件 (告知 Knife4j 使用 Bearer Token 规范)
                .components(new Components()
                        .addSecuritySchemes("BearerToken",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .name("Authorization")
                        ))
                // 2. 将鉴权组件应用到所有的接口上
                .addSecurityItem(new SecurityRequirement().addList("BearerToken"));
    }
}