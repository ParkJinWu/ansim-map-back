package com.ansim.map.global.security.jwt;

import com.ansim.map.global.exception.AnsimException;
import com.ansim.map.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds = 7 * 24 * 60 * 60 * 1000L; // 7일 고정

    public JwtUtils(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        // 기존 24시간 대신 설정 파일의 값을 따르도록 변경
        this.accessTokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    /**
     * Access Token 생성 (짧은 수명 + 권한 정보 포함)
     */
    public String generateToken(String email, Long id, String role) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(email)
                .claim("id", id)
                .claim("auth", role)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Refresh Token 생성 (긴 수명 + 식별 정보만 포함)
     */
    public String generateRefreshToken(String email) {
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 유효성 검증
     */
    public void validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
        } catch (SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            // 위조되었거나 형식이 잘못된 토큰
            throw new AnsimException(ErrorCode.INVALID_TOKEN);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰
            throw new AnsimException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    /**
     * Claims 추출 시에도 검증 로직을 타게 되므로 예외 처리 통합 가능
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            // 만료된 경우라도 이메일 추출이 필요한 로그아웃 등을 위해 Claims 반환
            return e.getClaims();
        } catch (Exception e) {
            // 그 외의 경우는 모두 유효하지 않은 토큰으로 간주
            throw new AnsimException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

}