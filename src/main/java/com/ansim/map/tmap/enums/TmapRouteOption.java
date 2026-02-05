package com.ansim.map.tmap.enums;

public enum TmapRouteOption {
    OPTIMAL("0", "최적 경로"),
    SMOOTH("1", "교통 원활"),
    SHORTEST("2", "최단 거리"),
    MAIN_ROAD("4", "대로 우선(안심)"),
    SHORTEST_FREE("10", "최단(유료제외)"),
    MOTORCYCLE("12", "이륜차(일반도로)"),
    OPTIMAL_FREE("19", "최적(무료)");

    private final String value;
    private final String description;

    TmapRouteOption(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() { return value; }
    public String getDescription() { return description; }
}