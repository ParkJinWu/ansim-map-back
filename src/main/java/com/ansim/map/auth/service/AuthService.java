package com.ansim.map.auth.service;

import com.ansim.map.auth.dto.AuthDto;
import com.ansim.map.auth.dto.TokenResponse;
import com.ansim.map.domain.entity.Member;
import com.ansim.map.enums.Role;
import com.ansim.map.member.repository.MemberRepository;
import com.ansim.map.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // JwtUtils 대신 Provider 사용 가능

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
    public TokenResponse login(AuthDto request) {
        // 1. 사용자 존재 여부 확인
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일을 확인해주세요."));

        // 2. 비밀번호 일치 여부 확인
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 토큰 발급 (Provider 활용)
        // 만약 jwtTokenProvider에 Authentication 객체가 필요하다면 로직이 살짝 달라질 수 있습니다.
        String accessToken = jwtTokenProvider.createToken(member.getEmail(), String.valueOf(member.getRole()));

        return new TokenResponse(accessToken, "Bearer");
    }
}