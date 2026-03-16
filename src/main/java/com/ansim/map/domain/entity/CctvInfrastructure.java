package com.ansim.map.domain.entity;

import com.ansim.map.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "cctv_infrastructure")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CctvInfrastructure extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String mngNo; // MNG_NO

    // PostGIS 공간 데이터 타입 매핑 (SRID 4326은 위경도 표준)
    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point location;

    private Integer cameraCount;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String managementOrg;       // 관리기관 표시용
}