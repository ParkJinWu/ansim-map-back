package com.ansim.map.recentPath.service;

import com.ansim.map.domain.entity.Member;
import com.ansim.map.domain.entity.RecentPath;
import com.ansim.map.global.exception.AnsimException;
import com.ansim.map.global.exception.ErrorCode;
import com.ansim.map.member.repository.MemberRepository;
import com.ansim.map.recentPath.dto.RecentPathRequest;
import com.ansim.map.recentPath.dto.RecentPathResponse;
import com.ansim.map.recentPath.repository.RecentPathRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecentPathService {

    private final RecentPathRepository recentPathRepository;
    private final MemberRepository memberRepository;

    private static final int MAX_LIMIT = 5;

    /**
     * 최근 경로 저장
     */
    public void addRecentPath(Long memberId, RecentPathRequest dto) {
        // memberId로 바로 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AnsimException(ErrorCode.MEMBER_NOT_FOUND));

        // 1. 중복 체크 및 기존 데이터 삭제
        recentPathRepository.findByMemberAndStartPlaceNameAndEndPlaceName(
                member, dto.getStartPlaceName(), dto.getEndPlaceName()
        ).ifPresent(recentPathRepository::delete);

        // 2. 개수 제한 체크 (10개)
        List<RecentPath> currentPaths = recentPathRepository.findAllByMemberOrderByUpdatedAtDesc(member);
        if (currentPaths.size() >= MAX_LIMIT) {
            recentPathRepository.delete(currentPaths.get(currentPaths.size() - 1));
        }

        // 3. 저장
        recentPathRepository.save(dto.toEntity(member));
    }

    /**
     * 최근 경로 목록 조회
     */
    @Transactional(readOnly = true)
    public List<RecentPathResponse> getRecentPaths(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AnsimException(ErrorCode.MEMBER_NOT_FOUND));

        return recentPathRepository.findAllByMemberOrderByUpdatedAtDesc(member)
                .stream()
                .map(RecentPathResponse::from)
                .toList();
    }

    /**
     * 최근 경로 개별 삭제
     */
    public void deletePath(Long id, Long memberId) {
        RecentPath path = recentPathRepository.findById(id)
                .orElseThrow(() -> new AnsimException(ErrorCode.NOT_FOUND));

        if (!path.getMember().getId().equals(memberId)) {
            throw new AnsimException(ErrorCode.ACCESS_DENIED);
        }

        recentPathRepository.delete(path);
    }

    /**
     * 최근 경로 전체 삭제
     */
    public void deleteAllPaths(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AnsimException(ErrorCode.MEMBER_NOT_FOUND));

        recentPathRepository.deleteAllByMember(member);
    }
}