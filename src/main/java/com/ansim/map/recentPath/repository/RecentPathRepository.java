package com.ansim.map.recentPath.repository;

import com.ansim.map.domain.entity.Member;
import com.ansim.map.domain.entity.RecentPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentPathRepository extends JpaRepository<RecentPath, Long> {

    // 사용자의 최근 경로 목록을 마지막 사용 시간(updatedAt) 내림차순으로 조회
    List<RecentPath> findAllByMemberOrderByUpdatedAtDesc(Member member);

    // 동일한 출발지명과 도착지명을 가진 경로가 이미 존재하는지 확인 (중복 체크용)
    Optional<RecentPath> findByMemberAndStartPlaceNameAndEndPlaceName(
            Member member, String startPlaceName, String endPlaceName
    );

    // 사용자별 전체 기록 삭제
    void deleteAllByMember(Member member);
}