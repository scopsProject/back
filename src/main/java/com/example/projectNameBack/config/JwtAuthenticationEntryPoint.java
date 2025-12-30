package com.example.projectNameBack.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // 401 에러를 명확하게 전송
        // 프론트엔드(api.js)의 interceptor가 이 401 상태 코드를 잡아서 처리하게 됩니다.
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "인증에 실패했습니다.");
    }
}