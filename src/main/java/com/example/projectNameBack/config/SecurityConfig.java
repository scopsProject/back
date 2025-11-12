package com.example.projectNameBack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.config.Customizer;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                // ⬇️ 이 부분이 corsConfigurationSource() 메서드를 참조합니다.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 🚨 여기에 다른 중요한 HTTP 요청 규칙이 더 필요할 수 있습니다. (예: .authorizeHttpRequests)
                // 지금은 CORS 문제에만 집중했지만,
                // 회원가입(/scops/userRegister) 경로가 인증 없이 접근 가능해야 합니다.
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/scops/userRegister", "/api/scops/login").permitAll() // 로그인, 회원가입 경로는 모두 허용
                        .anyRequest().authenticated() // 나머지 요청은 인증 필요 (예시)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ⬇️ ‼️ 여기가 수정된 핵심입니다. ‼️
        // .allowedOrigins() 대신 .allowedOriginPatterns()를 사용합니다.
        configuration.setAllowedOriginPatterns(List.of(
                "https://front-a3c.pages.dev",       // 1. 고정 프로덕션 URL
                "https://*.front-a3c.pages.dev",      // 2. 모든 미리보기 URL
                "http://localhost:3000",              // 3. 로컬 테스트용
                "https://scopsband.mooo.com:8080"
        ));

        // ⬇️ 나머지 설정은 그대로 유지합니다.
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 적용

        return source;
    }
}