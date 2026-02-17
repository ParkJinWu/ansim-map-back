package com.ansim.map.recentPath.dto;

import com.ansim.map.domain.entity.RecentPath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class RecentPathResponse {
    private Long id;
    private String startPlaceName;
    private String startAddressName;
    private String endPlaceName;
    private String endAddressName;
    private LocalDateTime lastUsedAt;

    public static RecentPathResponse from(RecentPath entity) {
        return RecentPathResponse.builder()
                .id(entity.getId())
                .startPlaceName(entity.getStartPlaceName())
                .startAddressName(entity.getStartAddressName())
                .endPlaceName(entity.getEndPlaceName())
                .endAddressName(entity.getEndAddressName())
                .lastUsedAt(entity.getUpdatedAt())
                .build();
    }
}