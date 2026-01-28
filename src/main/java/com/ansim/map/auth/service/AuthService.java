package com.ansim.map.auth.service;

import com.ansim.map.auth.dto.AuthDto;
import com.ansim.map.auth.dto.TokenResponse;
import com.ansim.map.domain.entity.Member;
import com.ansim.map.enums.Role;
import com.ansim.map.member.repository.MemberRepository;
import com.ansim.map.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;

    /**
     * 회원가입
     */
    @Transactional
    public void signUp(AuthDto request) {
        // 1. 중복 가입 방지
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 2. 패스워드 암호화 후 DB 저장
        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())  // name 추가
                .role(Role.USER)  // Role enum 사용
                .build();

        memberRepository.save(member);
    }

    /**
     * 로그인
     */
    @Transactional // Redis 저장이 포함되므로 Transactional 권장
    public TokenResponse login(AuthDto request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일을 확인해주세요."));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String email = member.getEmail();
        String role = String.valueOf(member.getRole());

        // 1. AccessToken 생성
        String accessToken = jwtTokenProvider.createToken(email, role);

        // 2. RefreshToken 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        // 3. Redis에 RefreshToken 저장 (Key: "RT:이메일", Value: 토큰값)
        // 7일간 유지되도록 설정
        redisTemplate.opsForValue().set(
                "RT:" + email,
                refreshToken,
                7, TimeUnit.DAYS
        );

        return new TokenResponse(accessToken, refreshToken, "Bearer");
    }

    /**
     * 로그아웃
     */
    @Transactional
    public void logout(String email) {
        // Redis에서 해당 이메일로 저장된 RefreshToken 삭제
        redisTemplate.delete("RT:" + email);
    }
}