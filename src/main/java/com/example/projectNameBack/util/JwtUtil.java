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

@Component // ⬅️ ‼️ 이 어노테이션이 있어야 Filter가 주입(DI)받을 수 있습니다. ‼️
public class JwtUtil {

    // ‼️ application.yml(또는 .properties)에 jwt.secret-key를 정의해야 합니다. ‼️
    @Value("${jwt.secret-key}")
    private String secretKeyString;

    private Key getSigningKey() {
        // Base64로 인코딩된 시크릿 키를 디코딩하여 Key 객체로 변환
        // (시크릿 키는 HMAC-SHA 알고리즘에 충분히 길어야 합니다)
        byte[] keyBytes = Base64.getDecoder().decode(secretKeyString);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- 토큰에서 정보 추출 ---

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

    // ‼️ User ID(Subject) 추출 (JwtAuthenticationFilter가 이 메서드를 사용합니다) ‼️
    public String getUserIdFromToken(String token) {
        return extractClaim(token, Claims::getSubject); // 'Subject'에 userId를 저장했다고 가정
    }

    // 토큰 만료 시간 추출
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // --- 토큰 검증 ---

    // 토큰이 만료되었는지 확인
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ‼️ 토큰 유효성 검증 (JwtAuthenticationFilter가 이 메서드를 사용합니다) ‼️
    public Boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도 (만료, 서명 오류 등 여기서 잡힘)
            extractAllClaims(token);
            return true; // 유효함
        } catch (Exception e) {
            return false; // 유효하지 않음
        }
    }

    // --- (참고) 로그인 성공 시 '토큰 생성' 로직 (로그인 컨트롤러에서 사용) ---
    // (이 메서드는 필요에 따라 수정하세요. 만료 시간 등)
    public String generateToken(String userId, String userName, UserRole role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userId) // ⬅️ 여기에 학번(userId)을 저장
                .claim("name", userName) // 🔥 사용자 이름 추가
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