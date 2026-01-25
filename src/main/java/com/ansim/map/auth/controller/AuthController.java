package com.ansim.map.auth.controller;

import com.ansim.map.auth.dto.AuthDto;
import com.ansim.map.auth.dto.TokenResponse;
import com.ansim.map.auth.service.AuthService;
import com.ansim.map.global.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입: DB에 유저 정보 저장
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody AuthDto request) {
        log.info("@@@");
        authService.signUp(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    /**
     * 로그인: DB 정보 확인 및 JWT 발급
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthDto request) {
        TokenResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}