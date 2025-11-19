package com.example.projectNameBack.config;

// ⬇️ 필요한 import 문 추가
import com.example.projectNameBack.config.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // ‼️ import 추가
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // ‼️ import 추가
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // ⬅️ CSRF 끄기
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // ⬇️ ‼️ JWT 사용을 위한 Stateless(무상태) 설정 ‼️
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authz -> authz
                        // ⬇️ ‼️ 로그인, 회원가입은 'permitAll' ‼️
                        .requestMatchers("/scops/userRegister", "/api/scops/login", "/scops/login", "/sse/subscribe", "/songs/**").permitAll()
                        .anyRequest().authenticated() // ⬅️ 나머지 모든 요청은 '인증' 필요
                )

                // ⬇️ ‼️ JWT 필터를 Security 필터 체인에 추가 ‼️
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ⬇️ ‼️ 'https://' 주소로 CORS 허용 ‼️
        configuration.setAllowedOriginPatterns(List.of(
                "https://front-a3c.pages.dev",
                "https://*.front-a3c.pages.dev",
                "http://localhost:3000",
                "https://scopsband.mooo.com" // ⬅️ Nginx https 주소
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}