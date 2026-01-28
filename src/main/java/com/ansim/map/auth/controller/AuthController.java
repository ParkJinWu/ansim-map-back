package com.ansim.map.auth.controller;

import com.ansim.map.auth.dto.AuthDto;
import com.ansim.map.auth.dto.TokenResponse;
import com.ansim.map.auth.service.AuthService;
import com.ansim.map.global.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody AuthDto request) {
        authService.signUp(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthDto request) {
        // 1. 서비스에서 토큰 뭉치(Access, Refresh)를 받아옴
        TokenResponse tokenResponse = authService.login(request);

        // 2. 리프레시 토큰을 위한 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(false)     // 로컬 환경에선 false (HTTPS는 true)
                .path("/")         // 모든 경로에서 사용
                .maxAge(7 * 24 * 60 * 60) // 7일 유지
                .sameSite("Lax")   // CSRF 방어
                .build();

        // 3. 헤더에는 쿠키를 넣고, 바디에는 액세스 토큰 정보만 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7).trim();

            try {
                String email = jwtUtils.getEmail(accessToken);
                authService.logout(email);
            } catch (Exception e) {
                log.error("====> 파싱 실패 원인: {}", e.getMessage());
            }
        }
        return ResponseEntity.noContent().build();
    }
}