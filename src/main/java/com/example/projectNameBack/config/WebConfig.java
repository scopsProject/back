package com.example.projectNameBack.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // API 경로

                // ⬇️ .allowedOrigins() 대신 .allowedOriginPatterns() 사용
                .allowedOriginPatterns(
                        // 1. 고정 프로덕션 URL 허용
                        "https://front-a3c.pages.dev",

                        // 2. 모든 미리보기 URL 허용 (예: ab9030b9.front-a3c.pages.dev)
                        "https://*.front-a3c.pages.dev",

                        // 3. 로컬 테스트용
                        "http://localhost:3000"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}