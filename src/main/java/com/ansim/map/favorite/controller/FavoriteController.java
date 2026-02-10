package com.ansim.map.favorite.controller;

import com.ansim.map.favorite.dto.FavoriteRequest;
import com.ansim.map.favorite.dto.FavoriteResponse;
import com.ansim.map.favorite.service.FavoriteService;
import com.ansim.map.global.common.ApiResponse;
import com.ansim.map.global.common.LoginUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/favorites")
@Slf4j
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public ApiResponse<List<FavoriteResponse>> getFavorites (@LoginUser Long memberId){
        List<FavoriteResponse> favorites = favoriteService.getFavorites(memberId);
        return ApiResponse.success(favorites);
    }

    @PostMapping
    public ApiResponse<FavoriteResponse> addFavorite(
            @LoginUser Long memberId,
            @RequestBody FavoriteRequest request) {

        FavoriteResponse favorite = favoriteService.addFavorite(memberId, request);
        return ApiResponse.success(favorite);
    }

    @PatchMapping("/{favoriteId}")
    public ApiResponse<FavoriteResponse> updateFavorite(
            @LoginUser Long memberId,
            @PathVariable Long favoriteId,
            @RequestBody FavoriteRequest request) {

        FavoriteResponse updated = favoriteService.updateFavorite(memberId, favoriteId, request);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{favoriteId}")
    public ApiResponse<Void> deleteFavorite(
            @LoginUser Long memberId,
            @PathVariable Long favoriteId) {

        favoriteService.deleteFavorite(memberId, favoriteId); // memberId를 같이 넘겨서 본인 확인
        return ApiResponse.success("삭제가 성공했습니다.");
    }
}
