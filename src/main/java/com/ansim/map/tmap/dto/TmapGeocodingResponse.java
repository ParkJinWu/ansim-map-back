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
        private String lat;
        private String lon;
        private String newLat;
        private String newLon;

        // 수동 생성을 위한 생성자 추가
        public Coordinate(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
            this.newLat = lat; // 둘 다 동일하게 세팅
            this.newLon = lon;
        }

        public String getBestLat() {
            return (newLat != null && !newLat.isEmpty()) ? newLat : lat;
        }

        public String getBestLon() {
            return (newLon != null && !newLon.isEmpty()) ? newLon : lon;
        }
    }
}