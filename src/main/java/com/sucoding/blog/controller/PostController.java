package com.sucoding.blog.controller;

import com.sucoding.blog.config.AppConstant;
import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.dto.post.PostRequest;
import com.sucoding.blog.dto.post.PostResponse;
import com.sucoding.blog.dto.post.PostVisibilityRequest;
import com.sucoding.blog.enums.MemberRole;
import com.sucoding.blog.security.CustomUserDetails;
import com.sucoding.blog.service.PostService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PostController {
    private final PostService postService;

    @SecurityRequirements
    @GetMapping("/public/posts")
    public ResponseEntity<PageResponse<PostResponse>> getAllPosts(
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_POST_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_POST_ASC) String sortOrder
    ) {
        PageResponse<PostResponse> response = postService.getAllPosts(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/posts")
    public ResponseEntity<PageResponse<PostResponse>> getAllPostsForAdmin(
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_POST_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_POST_ASC) String sortOrder
    ) {
        PageResponse<PostResponse> response = postService.getAllPostsForAdmin(pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public/posts/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {
        Long memberId = (userDetails != null) ? userDetails.getMemberId() : null;
        MemberRole role = (userDetails != null) ? userDetails.getRole() : null;
        PostResponse postResponse = postService.getPostById(memberId, postId, role);
        return ResponseEntity.ok(postResponse);
    }

    @GetMapping("/members/me/posts")
    public ResponseEntity<PageResponse<PostResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_POST_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_POST_ASC) String sortOrder
    ) {
        PageResponse<PostResponse> response = postService.getMyPosts(userDetails.getMemberId(), pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts")
    public ResponseEntity<PostResponse> createPost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.createPost(userDetails.getMemberId(), postRequest);
        return ResponseEntity.ok(postResponse);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest postRequest) {
        PostResponse updatedPost = postService.updatePost(userDetails.getMemberId(), postId, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @PatchMapping("/admin/posts/{postId}/visibility")
    public ResponseEntity<PostResponse> updatePostVisibility(
            @PathVariable Long postId,
            @Valid @RequestBody PostVisibilityRequest postVisibilityRequest) {
        PostResponse updatedPost = postService.updatePostVisibility(postId, postVisibilityRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId) {
        postService.deletePost(userDetails.getMemberId(), postId);
        return ResponseEntity.noContent().build();
    }
}
