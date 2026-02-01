package com.ansim.map.auth.service;

import com.ansim.map.auth.dto.AuthDto;
import com.ansim.map.auth.dto.TokenResponse;
import com.ansim.map.domain.entity.Member;
import com.ansim.map.enums.Role;
import com.ansim.map.global.exception.AnsimException;
import com.ansim.map.global.exception.ErrorCode;
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
        if (memberRepository.existsByEmail(request.getEmail())) {
            throw new AnsimException(ErrorCode.EMAIL_DUPLICATION);
        }

        Member member = Member.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    /**
     * 로그인
     */
    @Transactional
    public TokenResponse login(AuthDto request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AnsimException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new AnsimException(ErrorCode.LOGIN_FAILED);
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

    @Transactional
    public void logout(String email) {
        // 만약 이메일이 비어있거나 잘못된 경우에 대한 예외처리를 추가할 수도 있습니다.
        if (email == null) {
            throw new AnsimException(ErrorCode.INVALID_TOKEN);
        }
        redisTemplate.delete("RT:" + email);
    }
}