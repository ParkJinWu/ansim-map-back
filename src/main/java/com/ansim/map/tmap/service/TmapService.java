package com.ansim.map.tmap.service;

import com.ansim.map.tmap.dto.*;
import com.ansim.map.tmap.enums.TmapRouteOption;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class TmapService {

    private final WebClient tmapWebClient;

    // 7개 옵션을 한 번에 가져오는 서비스
    public Mono<List<TmapCarRouteResponse>> getCarRoutes(String sx, String sy, String ex, String ey) {
        // 우리가 사용할 옵션들 정의
        List<TmapRouteOption> targetOptions = List.of(
                TmapRouteOption.OPTIMAL,
                TmapRouteOption.SMOOTH,
                TmapRouteOption.SHORTEST,
                TmapRouteOption.MAIN_ROAD,
                TmapRouteOption.SHORTEST_FREE,
                TmapRouteOption.MOTORCYCLE,
                TmapRouteOption.OPTIMAL_FREE
        );

        // Stream을 사용하여 가독성 있게 Mono 리스트 생성
        List<Mono<TmapCarRouteResponse>> monos = targetOptions.stream()
                .map(option -> fetchRoute(sx, sy, ex, ey, option.getValue()))
                .collect(Collectors.toList());

        // Mono.zip으로 모든 요청 실행
        return Mono.zip(monos, results -> {
            List<TmapCarRouteResponse> list = new ArrayList<>();
            for (Object obj : results) {
                list.add((TmapCarRouteResponse) obj);
            }
            return list;
        });
    }

    // 공통 요청 메서드
    private Mono<TmapCarRouteResponse> fetchRoute(String sx, String sy, String ex, String ey, String option) {
        Map<String, Object> body = new HashMap<>();
        body.put("startX", sx);
        body.put("startY", sy);
        body.put("endX", ex);
        body.put("endY", ey);
        body.put("reqCoordType", "WGS84GEO");
        body.put("resCoordType", "WGS84GEO");
        body.put("searchOption", option);

        return tmapWebClient.post()
                .uri("/tmap/routes?version=1")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TmapCarRouteResponse.class);
    }

    /**
     * 주소 기반 통합 경로 검색 로직
     */
    public Mono<List<TmapCarRouteResponse>> getCarRoutesByAddress(String startAddr, String endAddr) {
        // 1. 출발지 지오코딩과 목적지 지오코딩을 동시에 실행
        return Mono.zip(fetchGeocoding(startAddr), fetchGeocoding(endAddr))
                .flatMap(coords -> {
                    TmapGeocodingResponse.Coordinate start = coords.getT1();
                    TmapGeocodingResponse.Coordinate end = coords.getT2();

                    // 2. 변환된 좌표로 기존 경로 검색 메서드 호출
                    return getCarRoutes(
                            start.getBestLon(), start.getBestLat(),
                            end.getBestLon(), end.getBestLat()
                    ).map(routes -> {
                        // 각 경로 응답 객체에 지오코딩으로 얻은 좌표를 주입
                        routes.forEach(route -> {
                            route.setStartLat(start.getBestLat());
                            route.setStartLon(start.getBestLon());
                            route.setEndLat(end.getBestLat());
                            route.setEndLon(end.getBestLon());
                        });
                        return routes;
                    });
                });
    }

    /**
     * 보행자 경로 검색 (좌표 기반)
     */
    public Mono<TmapPedestrianResponse> getPedestrianRoute(String sx, String sy, String ex, String ey) {
        Map<String, Object> body = new HashMap<>();
        body.put("startX", sx);
        body.put("startY", sy);
        body.put("endX", ex);
        body.put("endY", ey);
        body.put("startName", "출발지");
        body.put("endName", "목적지");
        body.put("reqCoordType", "WGS84GEO");
        body.put("resCoordType", "WGS84GEO");
        body.put("sort", "index"); // 경로 순서대로 정렬

        return tmapWebClient.post()
                .uri("/tmap/routes/pedestrian?version=1")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TmapPedestrianResponse.class)
                .doOnNext(res -> log.info("[Tmap API] 보행자 경로 수신 완료"));
    }

    /**
     * 주소 기반 보행자 경로 검색
     */
    public Mono<TmapPedestrianResponse> getPedestrianRouteByAddress(String startAddr, String endAddr) {
        return Mono.zip(fetchGeocoding(startAddr), fetchGeocoding(endAddr))
                .flatMap(coords -> {
                    TmapGeocodingResponse.Coordinate start = coords.getT1();
                    TmapGeocodingResponse.Coordinate end = coords.getT2();

                    return getPedestrianRoute(
                            start.getBestLon(), start.getBestLat(),
                            end.getBestLon(), end.getBestLat()
                    );
                });
    }

    // 지오코딩 단일 요청 메서드
    private Mono<TmapGeocodingResponse.Coordinate> fetchGeocoding(String address) {
        // 1. 좌표 형식인지 확인 ("경도,위도")
        if (address.contains(",") && address.split(",").length == 2) {
            String[] coords = address.split(",");
            String lon = coords[0].trim();
            String lat = coords[1].trim();

            // 생성자를 통해 API 호출 없이 바로 반환
            return Mono.just(new TmapGeocodingResponse.Coordinate(lat, lon));
        }
        log.info("[지오코딩 시작] 주소: {}", address);

        // 2. 좌표 형식이 아니면 기존처럼 API 호출
        return tmapWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tmap/geo/fullAddrGeo")
                        .queryParam("version", 1)
                        .queryParam("fullAddr", address)
                        .queryParam("coordType", "WGS84GEO")
                        .build())
                .retrieve()
                .bodyToMono(TmapGeocodingResponse.class)
                .flatMap(res -> {
                    if (res.getCoordinateInfo() == null || res.getCoordinateInfo().getCoordinate().isEmpty()) {
                        return Mono.error(new RuntimeException("주소를 찾을 수 없습니다: " + address));
                    }
                    TmapGeocodingResponse.Coordinate coord = res.getCoordinateInfo().getCoordinate().get(0);

                    // ✅ 변환 완료 후 로그 (주소 -> 좌표)
                    log.info("[지오코딩 완료] {} -> 경도(Lon): {}, 위도(Lat): {}",
                            address, coord.getBestLon(), coord.getBestLat());

                    return Mono.just(coord);
                });
    }

    public Mono<List<Map<String, Object>>> searchPoi(String keyword) {
        return tmapWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tmap/pois")
                        .queryParam("version", 1)
                        .queryParam("searchKeyword", keyword)
                        .queryParam("count", 10)
                        .queryParam("resCoordType", "WGS84GEO")
                        .build())
                .retrieve()
                .bodyToMono(TmapPoiResponse.class)
                .map(res -> {
                    if (res.getSearchPoiInfo() == null || res.getSearchPoiInfo().getPois() == null) {
                        return new ArrayList<>();
                    }

                    return res.getSearchPoiInfo().getPois().getPoi().stream()
                            .map(poi -> {
                                Map<String, Object> map = new HashMap<>();
                                map.put("id", poi.getId());
                                map.put("name", poi.getName());
                                map.put("fullAddress", poi.getFullAddress());
                                map.put("frontLat", poi.getFrontLat());
                                map.put("frontLon", poi.getFrontLon());
                                return map;
                            })
                            .collect(Collectors.toList());
                });
    }

    /**
     * POI 상세 정보 조회
     */
    public Mono<TmapPoiDetailResponse.PoiDetailInfo> getPoiDetail(String poiId) {
        return tmapWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/tmap/pois/" + poiId)
                        .queryParam("version", 1)
                        .queryParam("resCoordType", "WGS84GEO")
                        .build())
                .retrieve()
                .bodyToMono(TmapPoiDetailResponse.class)
                .map(TmapPoiDetailResponse::getPoiDetailInfo);
    }
}