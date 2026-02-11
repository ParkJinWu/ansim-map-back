package com.ansim.map.tmap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class TmapPoiResponse {
    private SearchPoiInfo searchPoiInfo;

    @Getter
    @NoArgsConstructor
    public static class SearchPoiInfo {
        private Pois pois;
    }

    @Getter
    @NoArgsConstructor
    public static class Pois {
        private List<Poi> poi;
    }

    @Getter
    @NoArgsConstructor
    public static class Poi {
        private String name;           // 장소명
        private String upperAddrName;  // 시/도
        private String middleAddrName; // 구/군
        private String lowerAddrName;  // 동/읍/면
        private String frontLat;       // 위도
        private String frontLon;       // 경도

        public String getFullAddress() {
            return upperAddrName + " " + middleAddrName + " " + lowerAddrName;
        }
    }
}