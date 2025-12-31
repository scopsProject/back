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

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. "Authorization" 헤더에서 토큰을 가져옵니다.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userId;
        final String name;
        final String role;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Bearer 부분을 잘라내고 순수 토큰추출
        jwt = authHeader.substring(7);

        try {
            // 토큰에서 userId 추출합니다.
            if (jwtUtil.validateToken(jwt)) {

                userId = jwtUtil.getUserIdFromToken(jwt);
                name = jwtUtil.getNameFromToken(jwt);
                role = jwtUtil.getRoleFromToken(jwt);

                if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    UserDetails userDetails = org.springframework.security.core.userdetails.User
                            .withUsername(userId)
                            .password("")
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
            logger.warn("JWT Token processing error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}