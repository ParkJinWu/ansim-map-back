package com.ansim.map.tmap.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TmapPoiDetailResponse {
    private PoiDetailInfo poiDetailInfo;

    @Getter
    @NoArgsConstructor
    public static class PoiDetailInfo {
        private String id;              // POI ID
        private String name;            // 장소명
        private String address;         // 표출 주소
        private String firstNo;         // 지번 본번
        private String secondNo;        // 지번 부번
        private String bizName;         // 업종명 (대분류)
        private String upperBizName;    // 업종명 (중분류)
        private String middleBizName;   // 업종명 (소분류)
        private String tel;             // 전화번호
        private String desc;            // 장소 설명
        private String lat;             // 위도
        private String lon;             // 경도
        private String additionalInfo;   // 추가 정보 (주차 가능 여부 등)
    }
}