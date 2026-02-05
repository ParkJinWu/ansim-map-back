package com.ansim.map.tmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapCarRouteResponse {
    private String type; // FeatureCollection
    private List<Feature> features;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Feature {
        private String type; // Feature
        private Geometry geometry;
        private Properties properties;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Geometry {
        private String type; // Point 또는 LineString
        private Object coordinates; // Point면 Double[], LineString이면 Double[][]
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Properties {
        private int totalTime;     // 총 소요 시간 (초)
        private int totalDistance; // 총 거리 (미터)
        private int totalFare;     // 총 요금 (원)
        private String name;       // 도로명 또는 지점 명칭
        private String description; // 안내 문구
        private int taxiFare;      // 예상 택시 요금 (안심맵에 유용!)
    }
}