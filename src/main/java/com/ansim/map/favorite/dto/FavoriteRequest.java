package com.ansim.map.favorite.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class FavoriteRequest {
    private String alias;
    private String addressName;
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
}