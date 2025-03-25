package com.ddobang.backend.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringdocConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("또방 API 명세서")
                        .description("또방 프로젝트의 백엔드 REST API 문서입니다.")
                        .version("v1.0.0"));
    }
}
