package com.ansim.map.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthDto {
    private String email;
    private String password;
    private String nickname; // 로그인 시에는 null이어도 상관없음
    private String name;  // 추가된 필드

}