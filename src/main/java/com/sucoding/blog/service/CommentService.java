package com.sucoding.blog.service;

import com.sucoding.blog.dto.comment.CommentRequest;
import com.sucoding.blog.dto.comment.CommentResponse;
import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.exceptions.custom.CommentNotFoundException;
import com.sucoding.blog.exceptions.custom.ForbiddenException;
import com.sucoding.blog.exceptions.custom.MemberNotFoundException;
import com.sucoding.blog.exceptions.custom.PostNotFoundException;
import com.sucoding.blog.mapper.CommentMapper;
import com.sucoding.blog.model.Comment;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.Post;
import com.sucoding.blog.repository.CommentRepository;
import com.sucoding.blog.repository.MemberRepository;
import com.sucoding.blog.repository.PostRepository;
import com.sucoding.blog.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private static final List<String> SORTABLE_FIELDS = List.of("id", "content", "createdAt");
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentMapper commentMapper;

    public PageResponse<CommentResponse> getCommentsByPostId(Long postId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Post post = postRepository.findByIdAndIsVisibleTrue(postId)
                .orElseThrow(PostNotFoundException::new);

        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Comment> commentPage = commentRepository.findByPost(post, pageDetails);
        List<Comment> comments = commentPage.getContent();
        return PageResponse.of(commentPage, commentMapper.toResponseList(comments));
    }

    public PageResponse<CommentResponse> getCommentsByPostIdForAdmin(Long postId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Comment> commentPage = commentRepository.findByPost(post, pageDetails);
        List<Comment> comments = commentPage.getContent();
        return PageResponse.of(commentPage, commentMapper.toResponseList(comments));
    }

    @Transactional
    public CommentResponse createComment(Long memberId, Long postId, CommentRequest commentRequest) {
        Post post = postRepository.findByIdAndIsVisibleTrue(postId)
                .orElseThrow(PostNotFoundException::new);

        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Comment comment = commentMapper.toEntity(commentRequest);
        comment.setPost(post);
        comment.setMember(member);
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toResponse(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long memberId, Long commentId, CommentRequest commentRequest) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("내 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(commentRequest.getContent());
        return commentMapper.toResponse(comment);
    }

    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getMember().getId().equals(memberId)) {
            throw new ForbiddenException("내 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }
}
