package com.ansim.map.member.controller;

import com.ansim.map.member.dto.MemberResponse;
import com.ansim.map.member.dto.ProfileUpdateDto;
import com.ansim.map.global.common.ApiResponse;
import com.ansim.map.global.common.LoginUser;
import com.ansim.map.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberProfile(
            @PathVariable Long memberId) {

        MemberResponse response = memberService.getMemberById(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<MemberResponse>> updateProfile(
            @LoginUser String email,
            @RequestBody ProfileUpdateDto request) {
        MemberResponse response = memberService.updateMemberProfile(email, request.getName(), request.getProfileImageUrl());
        return ResponseEntity.ok(ApiResponse.success("프로필 수정이 완료되었습니다.", response));
    }
}