package com.sucoding.blog.controller;

import com.sucoding.blog.dto.likes.LikesResponse;
import com.sucoding.blog.security.CustomUserDetails;
import com.sucoding.blog.service.LikesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikesController {
    private final LikesService likesService;

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<LikesResponse> createLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {
        LikesResponse status = likesService.createLike(userDetails.getMemberId(), postId);
        return ResponseEntity.ok(status);
    }

    @DeleteMapping("/posts/{postId}/likes")
    public ResponseEntity<LikesResponse> deleteLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {
        LikesResponse status = likesService.deleteLike(userDetails.getMemberId(), postId);
        return ResponseEntity.ok(status);
    }
}
