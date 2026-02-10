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
        Long memberId = member.getId();
        String role = String.valueOf(member.getRole());

        String accessToken = jwtTokenProvider.createToken(email, memberId, role);

        String refreshToken = jwtTokenProvider.createRefreshToken(email);

        redisTemplate.opsForValue().set(
                "RT:" + memberId,
                refreshToken,
                7, TimeUnit.DAYS
        );

        return TokenResponse.from(accessToken, refreshToken, "Bearer", member);
    }

    @Transactional
    public void logout(Long memberId) {
        if (memberId == null) {
            throw new AnsimException(ErrorCode.INVALID_TOKEN);
        }
        redisTemplate.delete("RT:" + memberId);
    }
}