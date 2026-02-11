package com.ansim.map.tmap.controller;

import com.ansim.map.tmap.dto.TmapCarRouteResponse;
import com.ansim.map.tmap.dto.TmapPoiResponse;
import com.ansim.map.tmap.service.TmapService;
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
            @RequestParam String startAddr,
            @RequestParam String endAddr
    ) {
        log.info("[주소 기반 경로 탐색] {} -> {}", startAddr, endAddr);
        return tmapService.getCarRoutesByAddress(startAddr, endAddr);
    }

    /**
     * TMAP POI 검색
     */
    @GetMapping("/search/poi")
    public Mono<List<TmapPoiResponse.Poi>> searchPoi(@RequestParam String keyword) {
        log.info("🔍 [장소 검색] 키워드: {}", keyword);
        return tmapService.searchPoi(keyword);
    }
}