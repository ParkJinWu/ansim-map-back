package com.ansim.map.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {
    private String token;
    private String type;

    public TokenResponse(String token, String type) {
        this.token = token;
        this.type = type;
    }
}