package com.sucoding.blog.service;

import com.sucoding.blog.dto.likes.LikesResponse;
import com.sucoding.blog.exceptions.custom.DuplicateLikesException;
import com.sucoding.blog.exceptions.custom.LikesNotFoundException;
import com.sucoding.blog.exceptions.custom.MemberNotFoundException;
import com.sucoding.blog.exceptions.custom.PostNotFoundException;
import com.sucoding.blog.model.Likes;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.Post;
import com.sucoding.blog.repository.LikesRepository;
import com.sucoding.blog.repository.MemberRepository;
import com.sucoding.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikesService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final LikesRepository likesRepository;

    @Transactional
    public LikesResponse createLike(Long memberId, Long postId) {
        Post post = postRepository.findByIdAndIsVisibleTrue(postId)
                .orElseThrow(PostNotFoundException::new);

        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        if (likesRepository.existsByPostAndMember(post, member)) {
            throw new DuplicateLikesException();
        }

        Likes likes = new Likes();
        likes.setPost(post);
        likes.setMember(member);
        likesRepository.save(likes);

        LikesResponse response = new LikesResponse();
        response.setLikesCount(likesRepository.countByPostId(postId));
        response.setLiked(true);
        return response;
    }

    @Transactional
    public LikesResponse deleteLike(Long memberId, Long postId) {
        Likes likes = likesRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(LikesNotFoundException::new);

        likesRepository.delete(likes);

        LikesResponse response = new LikesResponse();
        response.setLikesCount(likesRepository.countByPostId(postId));
        response.setLiked(false);
        return response;
    }
}
