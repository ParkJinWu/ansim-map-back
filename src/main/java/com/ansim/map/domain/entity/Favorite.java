package com.ansim.map.domain.entity;

import com.ansim.map.favorite.dto.FavoriteRequest;
import com.ansim.map.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Favorite extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, length = 50)
    private String alias; // 별칭

    @Column(nullable = false)
    private String addressName; // 전체 주소

    private String placeName; // 장소 이름

    @Column(nullable = false)
    private String poiId; // Tmap에서 제공하는 고유

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal latitude;

    @Column(nullable = false, precision = 13, scale = 10)
    private BigDecimal longitude;

    public void update(FavoriteRequest request) {
        // 값이 들어온 것(not null)만 골라서 업데이트합니다.
        if (request.getAlias() != null) {
            this.alias = request.getAlias();
        }
        if (request.getAddressName() != null) {
            this.addressName = request.getAddressName();
        }
        if (request.getPlaceName() != null) {
            this.placeName = request.getPlaceName();
        }
        if (request.getLatitude() != null) {
            this.latitude = request.getLatitude();
        }
        if (request.getLongitude() != null) {
            this.longitude = request.getLongitude();
        }
    }
}
