package com.ansim.map.tmap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class TmapGeocodingResponse {
    private CoordinateInfo coordinateInfo;

    @Getter
    @NoArgsConstructor
    public static class CoordinateInfo {
        private List<Coordinate> coordinate;
    }

    @Getter
    @NoArgsConstructor
    public static class Coordinate {
        private String lat;     // 기본 위도
        private String lon;     // 기본 경도
        private String newLat;  // 도로명 주소 위도
        private String newLon;  // 도로명 주소 경도

        // 실제 경로 탐색에 사용할 위도 추출 (새 주소 우선)
        public String getBestLat() {
            return (newLat != null && !newLat.isEmpty()) ? newLat : lat;
        }

        // 실제 경로 탐색에 사용할 경도 추출 (새 주소 우선)
        public String getBestLon() {
            return (newLon != null && !newLon.isEmpty()) ? newLon : lon;
        }
    }
}