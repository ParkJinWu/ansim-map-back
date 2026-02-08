package com.ansim.map.member.service;

import com.ansim.map.domain.entity.Member;
import com.ansim.map.global.exception.AnsimException; // ✅ 커스텀 예외
import com.ansim.map.global.exception.ErrorCode;      // ✅ 에러 코드
import com.ansim.map.member.dto.MemberResponse;
import com.ansim.map.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMemberById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AnsimException(ErrorCode.MEMBER_NOT_FOUND, "Member ID: " + memberId));

        return MemberResponse.from(member);
    }

    public MemberResponse updateMemberProfile(String email, String name, String profileImageUrl) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new AnsimException(ErrorCode.MEMBER_NOT_FOUND));

        member.updateProfile(name, profileImageUrl);

        return MemberResponse.from(member);
    }


}