package com.ansim.map.member.dto;

import com.ansim.map.domain.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberResponse {
    private String email;
    private String name;
    private String profileImageUrl;
    private String role;

    // 엔티티를 DTO로 변환
    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .email(member.getEmail())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .role(member.getRole().name())
                .build();
    }
}