package com.ansim.map.tmap;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class TmapService {

    private final WebClient tmapWebClient;

    public Mono<TmapRouteResponse> getTransitPath(String sx, String sy, String ex, String ey) {
        Map<String, Object> body = new HashMap<>();
        body.put("startX", sx);
        body.put("startY", sy);
        body.put("endX", ex);
        body.put("endY", ey);
        body.put("count", 5);
        body.put("lang", 0); // 0: 국문, 1: 영문 (기본값 설정)
        body.put("format", "json");

        return tmapWebClient.post()
                .uri("/transit/routes")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(TmapRouteResponse.class);
    }
}