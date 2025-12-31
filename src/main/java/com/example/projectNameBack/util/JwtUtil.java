package com.example.projectNameBack.util;

import com.example.projectNameBack.entity.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component; // ⬅️ ‼️ 필수 ‼️

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    private Key getSigningKey() {
        // Base64로 인코딩된 시크릿 키를 디코딩하여 Key 객체로 변환
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 모든 Claim 추출
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 특정 Claim 추출
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // User ID(Subject) 추출
    public String getUserIdFromToken(String token) {
        return extractClaim(token, Claims::getSubject); // 'Subject'에 userId를 저장했다고 가정
    }

    // 토큰 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 토큰 유효성 검증
    public Boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도 (만료, 서명 오류 등 여기서 잡힘)
            extractAllClaims(token);
            return true; // 유효함
        } catch (Exception e) {
            return false; // 유효하지 않음
        }
    }

    // 로그인 성공 시 '토큰 생성' 로직
    public String generateToken(String userId, String userName, UserRole role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userId)
                .claim("name", userName)
                .claim("role", role)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + 1000 * 60 * 60)) // 1시간 후 만료
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String getNameFromToken(String token) {
        return extractClaim(token, claims -> claims.get("name", String.class));
    }

    public String getRoleFromToken(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}