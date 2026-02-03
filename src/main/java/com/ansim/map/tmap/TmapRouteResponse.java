package com.ansim.map.tmap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TmapRouteResponse {
    private MetaData metaData;

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MetaData {
        private Plan plan;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Plan {
        private List<Itinerary> itineraries;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Itinerary {
        private int totalTime;
        private int totalDistance;
        private int totalWalkTime;
        private int totalWalkDistance;
        private List<Leg> legs;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Leg {
        private String mode; // WALK, BUS, SUBWAY
        private String sectionTime;
        private int distance;
        private Object passShape;
        private String route; // 버스 번호나 지하철 호선 이름
        private List<Step> steps; // 도보 이동 시 상세 안내
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Step {
        private String description;
        private String linestring; // 도보 구간의 좌표
    }
}