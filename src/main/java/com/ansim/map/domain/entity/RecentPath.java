package com.ansim.map.domain.entity;

import com.ansim.map.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RecentPath extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 출발지 정보
    @Column(nullable = false)
    private String startPlaceName;

    private String startAddressName;

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal startLatitude;

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal startLongitude;

    // 목적지 정보
    @Column(nullable = false)
    private String endPlaceName;

    private String endAddressName;

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal endLatitude;

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal endLongitude;
}