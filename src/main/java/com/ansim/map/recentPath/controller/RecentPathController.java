package com.ansim.map.recentPath.controller;

import com.ansim.map.global.common.ApiResponse;
import com.ansim.map.global.common.LoginUser;
import com.ansim.map.recentPath.dto.RecentPathRequest;
import com.ansim.map.recentPath.dto.RecentPathResponse;
import com.ansim.map.recentPath.service.RecentPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recent-paths")
@RequiredArgsConstructor
public class RecentPathController {
    private final RecentPathService recentPathService;

    @PostMapping
    public ApiResponse<Void> create(@LoginUser Long memberId, @RequestBody RecentPathRequest dto) {
        recentPathService.addRecentPath(memberId, dto);
        return ApiResponse.success(null);
    }

    @GetMapping
    public ApiResponse<List<RecentPathResponse>> getList(@LoginUser Long memberId) {
        List<RecentPathResponse> data = recentPathService.getRecentPaths(memberId);
        return ApiResponse.success(data);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteOne(@LoginUser Long memberId, @PathVariable Long id) {
        recentPathService.deletePath(id, memberId);
        return ApiResponse.success("삭제되었습니다.");
    }

    @DeleteMapping
    public ApiResponse<Void> deleteAll(@LoginUser Long memberId) {
        recentPathService.deleteAllPaths(memberId);
        return ApiResponse.success("전체 삭제되었습니다.");
    }
}