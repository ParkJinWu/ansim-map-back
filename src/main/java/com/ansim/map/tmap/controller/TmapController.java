package com.ansim.map.tmap.controller;

import com.ansim.map.tmap.TmapCarRouteResponse;
import com.ansim.map.tmap.TmapService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/tmap")
public class TmapController {

    private final TmapService tmapService;

    /**
     * TMAP 자동차 경로 검색
     */
    @GetMapping("/path/car")
    public Mono<List<TmapCarRouteResponse>> getCarPath(
            @RequestParam String sx, @RequestParam String sy,
            @RequestParam String ex, @RequestParam String ey
    ) {
        log.info("🚗 [다중 경로 탐색] 최적길 & 대로길 동시 요청");
        return tmapService.getCarRoutes(sx, sy, ex, ey);
    }
}