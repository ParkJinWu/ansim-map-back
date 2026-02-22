package com.ansim.map.tmap.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class TmapPedestrianResponse {
    private String type;
    private List<Feature> features;

    @Getter
    @Setter
    public static class Feature {
        private String type;
        private Geometry geometry;
        private Properties properties;
    }

    @Getter
    @Setter
    public static class Geometry {
        private String type;
        private List<Object> coordinates; // [lon, lat] 또는 [[lon, lat], ...]
    }

    @Getter
    @Setter
    public static class Properties {
        private Integer totalDistance;
        private Integer totalTime;
        private String name;
        private Integer index;
        private Integer pointIndex;
        private Integer lineIndex;
        private String description;
        private Integer turnType; // 횡단보도 등 길 안내 타입
        private Integer facilityType; // 1:교량, 2:터널, 3:지하도, 11:육교, 12:계단 등

        // 안심 점수 산출 후 주입할 필드 (커스텀)
        private Integer safetyScore;
        private Boolean isAnsimBest;
    }
}