package com.sucoding.blog.controller;

import com.sucoding.blog.config.AppConstant;
import com.sucoding.blog.dto.comment.CommentRequest;
import com.sucoding.blog.dto.comment.CommentResponse;
import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.security.CustomUserDetails;
import com.sucoding.blog.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/public/posts/{postId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_CATEGORY_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_CATEGORY_ASC) String sortOrder) {
        PageResponse<CommentResponse> response = commentService.getCommentsByPostId(postId, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/posts/{postId}/comments")
    public ResponseEntity<PageResponse<CommentResponse>> getCommentsByPostIdForAdmin(
            @PathVariable Long postId,
            @RequestParam(value = "pageNumber", defaultValue = AppConstant.DEFAULT_PAGE_NUMBER) Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = AppConstant.DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstant.SORT_CATEGORY_BY) String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = AppConstant.SORT_CATEGORY_ASC) String sortOrder) {
        PageResponse<CommentResponse> response = commentService.getCommentsByPostIdForAdmin(postId, pageNumber, pageSize, sortBy, sortOrder);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.createComment(userDetails.getMemberId(), postId, commentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentResponse);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.updateComment(userDetails.getMemberId(), commentId, commentRequest);
        return ResponseEntity.ok(commentResponse);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long commentId) {
        commentService.deleteComment(userDetails.getMemberId(), commentId);
        return ResponseEntity.noContent().build();
    }
}
