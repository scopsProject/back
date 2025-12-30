package com.example.projectNameBack.config;
import com.example.projectNameBack.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // ⬅️ ‼️ Spring Bean으로 등록하기 위해 필수! ‼️
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // ⬅️ 'JwtUtil'을 주입받습니다.

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. "Authorization" 헤더에서 토큰을 가져옵니다.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId; // ⬅️ 'userId' (학번)을 추출할 것입니다.
        final String name;
        final String role;

        // 2. 헤더가 없거나 "Bearer "로 시작하지 않으면, 토큰이 없는 요청이므로 그냥 통과시킵니다.
        // (이후 SecurityConfig가 /login 같은 공개 경로는 허용하고, 보호된 경로는 막을 것입니다.)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. "Bearer " 부분을 잘라내고 순수 토큰(jwt)만 추출합니다.
        jwt = authHeader.substring(7);

        try {
            // 4. JwtUtil을 사용해 토큰을 검증하고, 토큰에서 'userId' (학번)을 추출합니다.
            // (JwtUtil에 validateToken, getUserIdFromToken 메서드가 필요합니다.)
            if (jwtUtil.validateToken(jwt)) {

                userId = jwtUtil.getUserIdFromToken(jwt);
                name = jwtUtil.getNameFromToken(jwt);
                role = jwtUtil.getRoleFromToken(jwt);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(userId)
                            .password("") // 필요 없음
                            .authorities(role)
                            .build();

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            // 토큰 검증 실패 시 (예: 만료, 서명 오류)
            // 그냥 통과시키면 SecurityContext가 비어있으므로, '인증되지 않은' 사용자로 처리됩니다.
            logger.warn("JWT Token processing error: " + e.getMessage());
        }

        // 9. 다음 필터로 요청을 전달합니다.
        filterChain.doFilter(request, response);
    }
}