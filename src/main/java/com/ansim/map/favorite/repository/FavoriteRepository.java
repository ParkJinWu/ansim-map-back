package com.ansim.map.favorite.repository;

import com.ansim.map.domain.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite,Long> {
    // 특정 회원의 즐겨찾기 목록을 최신순으로 조회
    List<Favorite> findAllByMemberIdOrderByCreatedAtDesc(Long memberId);

    long countByMemberId(Long memberId);
}
