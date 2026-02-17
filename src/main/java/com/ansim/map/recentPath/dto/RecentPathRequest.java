package com.ansim.map.recentPath.dto;

import com.ansim.map.domain.entity.Member;
import com.ansim.map.domain.entity.RecentPath;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecentPathRequest {
    private String startPlaceName;
    private String startAddressName;
    private BigDecimal startLatitude;
    private BigDecimal startLongitude;

    private String endPlaceName;
    private String endAddressName;
    private BigDecimal endLatitude;
    private BigDecimal endLongitude;

    public RecentPath toEntity(Member member) {
        return RecentPath.builder()
                .member(member)
                .startPlaceName(this.startPlaceName)
                .startAddressName(this.startAddressName)
                .startLatitude(this.startLatitude)
                .startLongitude(this.startLongitude)
                .endPlaceName(this.endPlaceName)
                .endAddressName(this.endAddressName)
                .endLatitude(this.endLatitude)
                .endLongitude(this.endLongitude)
                .build();
    }
}