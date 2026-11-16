package com.sucoding.blog.controller;

import com.sucoding.blog.controller.docs.ProfileControllerDocs;
import com.sucoding.blog.dto.profile.ProfileRequest;
import com.sucoding.blog.dto.profile.ProfileResponse;
import com.sucoding.blog.security.CustomUserDetails;
import com.sucoding.blog.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ProfileController implements ProfileControllerDocs {
    private final ProfileService profileService;

    @Override
    @GetMapping("/public/members/{memberId}/profile")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long memberId) {
        ProfileResponse profile = profileService.getProfileByMemberId(memberId);
        return ResponseEntity.ok(profile);
    }
    
    @Override
    @PutMapping("/members/me/profile")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileRequest profileRequest
    ) {
        ProfileResponse response = profileService.updateMyProfile(userDetails.getMemberId(), profileRequest);
        return ResponseEntity.ok(response);
    }
}
