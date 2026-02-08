package com.ansim.map.auth.dto;

import com.ansim.map.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String grantType;
    private Long memberId;
    private String email;
    private String name;

    public static TokenResponse from(String accessToken, String refreshToken, String grantType, Member member) {
        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .grantType(grantType)
                .memberId(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .build();
    }
}