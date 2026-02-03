package com.ansim.map.controller;

import com.ansim.map.tmap.TmapRouteResponse;
import com.ansim.map.tmap.TmapService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api")
public class TestController {

    private final TmapService tmapService;

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "안심맵 백엔드 연결 성공! 🛡️");
        response.put("version", "v1.0");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/v1/path/transit")
    public Mono<TmapRouteResponse> getTransitPath(
            @RequestParam String sx,
            @RequestParam String sy,
            @RequestParam String ex,
            @RequestParam String ey) {

        log.info("🚀 TMAP 경로 검색 요청: sx={}, sy={} -> ex={}, ey={}", sx, sy, ex, ey);
        return tmapService.getTransitPath(sx, sy, ex, ey);
    }
}

