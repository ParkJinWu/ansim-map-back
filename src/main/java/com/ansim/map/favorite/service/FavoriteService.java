package com.ansim.map.favorite.service;

import com.ansim.map.domain.entity.Favorite;
import com.ansim.map.domain.entity.Member;
import com.ansim.map.favorite.dto.FavoriteRequest;
import com.ansim.map.favorite.dto.FavoriteResponse;
import com.ansim.map.favorite.repository.FavoriteRepository;
import com.ansim.map.global.exception.AnsimException;
import com.ansim.map.global.exception.ErrorCode;
import com.ansim.map.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository; // Member 조회를 위해 필요

    @Transactional
    public FavoriteResponse addFavorite(Long memberId, FavoriteRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AnsimException(ErrorCode.MEMBER_NOT_FOUND));

        long favoriteCount = favoriteRepository.countByMemberId(memberId);

        // 최대 즐겨찾기 제한
        if(favoriteCount >= 5){
            throw new AnsimException(ErrorCode.FAVORITE_LIMIT_EXCEEDED);
        }

        Favorite favorite = Favorite.builder()
                .member(member)
                .alias(request.getAlias())
                .addressName(request.getAddressName())
                .placeName(request.getPlaceName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();

        return FavoriteResponse.from(favoriteRepository.save(favorite));
    }

    public List<FavoriteResponse> getFavorites(Long memberId) {
        return favoriteRepository.findAllByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(FavoriteResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteFavorite(Long memberId, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new AnsimException(ErrorCode.FAVORITE_NOT_FOUND));

        // 본인 소유 확인
        if (!favorite.getMember().getId().equals(memberId)) {
            throw new AnsimException(ErrorCode.ACCESS_DENIED);
        }

        favoriteRepository.delete(favorite);
    }

    @Transactional
    public FavoriteResponse updateFavorite(Long memberId, Long favoriteId, FavoriteRequest request) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new AnsimException(ErrorCode.FAVORITE_NOT_FOUND));

        // 본인 소유 확인
        if (!favorite.getMember().getId().equals(memberId)) {
            throw new AnsimException(ErrorCode.ACCESS_DENIED);
        }

        // 데이터 업데이트
        favorite.update(request);

        return FavoriteResponse.from(favorite);
    }
}
