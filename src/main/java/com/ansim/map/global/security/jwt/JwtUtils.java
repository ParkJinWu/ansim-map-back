package com.ansim.map.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey key;
    private final long tokenValidityInMilliseconds;

    public JwtUtils(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.token-validity-in-seconds}") long tokenValidityInSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.tokenValidityInMilliseconds = tokenValidityInSeconds * 1000;
    }

    // 토큰 생성의 핵심 로직
    public String generateToken(String email, String role) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + 86400000); // 24시간 예시

        return Jwts.builder()
                .setSubject(email)                 // 이메일을 Subject로 저장
                .claim("auth", role)               // 권한 정보를 Custom Claim으로 저장
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰 파싱 및 클레임 추출
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 단순 검증
    public void validateToken(String token) {
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}