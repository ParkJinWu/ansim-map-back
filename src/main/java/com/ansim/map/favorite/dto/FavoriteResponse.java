package com.ansim.map.favorite.dto;

import com.ansim.map.domain.entity.Favorite;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class FavoriteResponse {
    private Long id;
    private String poiId;
    private String alias;
    private String addressName;
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;

    public static FavoriteResponse from(Favorite favorite) {
        return FavoriteResponse.builder()
                .id(favorite.getId())
                .poiId(favorite.getPoiId())
                .alias(favorite.getAlias())
                .addressName(favorite.getAddressName())
                .placeName(favorite.getPlaceName())
                .latitude(favorite.getLatitude())
                .longitude(favorite.getLongitude())
                .build();
    }
}