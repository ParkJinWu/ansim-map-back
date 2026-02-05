package com.ansim.map.tmap;

import com.ansim.map.tmap.enums.TmapRouteOption;
import lombok.AllArgsConstructor;
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
}