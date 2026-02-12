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
        private String name;
        private String upperAddrName;
        private String middleAddrName;
        private String lowerAddrName;
        private String frontLat;
        private String frontLon;

        // 티맵은 도로명 주소를 이 리스트 안에 담아줍니다.
        private NewAddressList newAddressList;

        @Getter
        @NoArgsConstructor
        public static class NewAddressList {
            private List<NewAddress> newAddress;
        }

        @Getter
        @NoArgsConstructor
        public static class NewAddress {
            private String centerLat;
            private String centerLon;
            private String frontLat;
            private String frontLon;
            private String roadName;      // 도로명 (예: 올림픽로)
            private String bldNo1;       // 건물 본번 (예: 240)
            private String bldNo2;       // 건물 부번
        }

        public String getFullAddress() {
            // 1. 도로명 주소 리스트가 있는지 확인
            if (newAddressList != null && newAddressList.getNewAddress() != null && !newAddressList.getNewAddress().isEmpty()) {
                NewAddress addr = newAddressList.getNewAddress().get(0);
                return String.format("%s %s %s %s",
                        upperAddrName, middleAddrName, addr.getRoadName(), addr.getBldNo1()).trim();
            }

            // 2. 도로명이 없으면 기존 방식(지번)으로 조합
            return String.format("%s %s %s",
                    upperAddrName, middleAddrName, lowerAddrName).trim();
        }
    }
}