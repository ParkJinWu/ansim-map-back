package com.ansim.map.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProfileUpdateDto {
    private String name;
    private String profileImageUrl; // S3 연동 전까지는 오픈 이미지 URL이 들어올 예정
}