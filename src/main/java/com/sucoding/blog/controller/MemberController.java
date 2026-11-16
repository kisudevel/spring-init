package com.sucoding.blog.controller;

import com.sucoding.blog.config.AppConstant;
import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.dto.member.*;
import com.sucoding.blog.security.CustomUserDetails;
import com.sucoding.blog.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/admin/members")
    public ResponseEntity<PageResponse<MemberResponse>> getAllMembers(
            @RequestParam(name = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstant.SORT_MEMBER_BY) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstant.SORT_MEMBER_ASC) String sortOrder
    ) {
        PageResponse<MemberResponse> members = memberService.getAllMembers(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(members);
    }

    @GetMapping("/public/members/{memberId}")
    public ResponseEntity<MemberPublicResponse> getMemberById(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberById(memberId));
    }

    @GetMapping("/members/me")
    public ResponseEntity<MemberMeResponse> getCurrentMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        MemberMeResponse memberMeResponse = memberService.getCurrentMember(userDetails.getMemberId());
        return ResponseEntity.ok(memberMeResponse);
    }

    @PutMapping("/members/me")
    public ResponseEntity<MemberPublicResponse> updateMember(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody MemberUpdateRequest memberRequest) {
        MemberPublicResponse updatedMember = memberService.updateMember(userDetails.getMemberId(), memberRequest);
        return ResponseEntity.ok(updatedMember);
    }

    @PatchMapping("/admin/members/{memberId}/role")
    public ResponseEntity<MemberAdminResponse> setRole(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long memberId,
            @Valid @RequestBody MemberRoleUpdateRequest roleUpdateRequest) {
        MemberAdminResponse updatedMember = memberService.setRole(userDetails.getMemberId(), memberId, roleUpdateRequest);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/members/me")
    public ResponseEntity<Void> deleteMember(@AuthenticationPrincipal CustomUserDetails userDetails) {
        memberService.deleteMember(userDetails.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/members/{memberId}")
    public ResponseEntity<Void> deleteMemberByAdmin(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
