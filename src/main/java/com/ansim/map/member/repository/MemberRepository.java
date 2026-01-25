package com.ansim.map.member.repository;

import com.ansim.map.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    // 이메일로 회원 존재 여부 확인 (로그인 시 사용)
    Optional<Member> findByEmail(String email);

    // 이메일 중복 가입 확인 (회원가입 시 사용)
    boolean existsByEmail(String email);
}
