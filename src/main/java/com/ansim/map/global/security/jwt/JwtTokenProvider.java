package com.ansim.map.global.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtUtils jwtUtils;

    public String createToken(String email, Long id, String role) {
        return jwtUtils.generateToken(email, id, role);
    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtUtils.getClaims(token);

        Long memberId = claims.get("id", Long.class);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, token, authorities);

        authentication.setDetails(memberId);

        return authentication;
    }

    public boolean validateToken(String token) {
        try {
            jwtUtils.validateToken(token);
            return true;
        } catch (Exception e) {
            log.info("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    public String createRefreshToken(String email) {
        // 만료 시간이 긴 리프레시 토큰 생성 로직 호출
        return jwtUtils.generateRefreshToken(email);
    }
}