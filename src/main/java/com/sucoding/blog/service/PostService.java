package com.sucoding.blog.service;

import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.dto.post.PostRequest;
import com.sucoding.blog.dto.post.PostResponse;
import com.sucoding.blog.dto.post.PostVisibilityRequest;
import com.sucoding.blog.enums.MemberRole;
import com.sucoding.blog.exceptions.custom.CategoryNotFoundException;
import com.sucoding.blog.exceptions.custom.ForbiddenException;
import com.sucoding.blog.exceptions.custom.MemberNotFoundException;
import com.sucoding.blog.exceptions.custom.PostNotFoundException;
import com.sucoding.blog.mapper.PostMapper;
import com.sucoding.blog.model.Category;
import com.sucoding.blog.model.Member;
import com.sucoding.blog.model.Post;
import com.sucoding.blog.repository.*;
import com.sucoding.blog.util.PageableFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private static final List<String> SORTABLE_FIELDS = List.of("id", "title", "content", "createdAt");
    private static final List<String> SORTABLE_MY_FIELDS = List.of("id", "title", "content", "isVisible", "createdAt");

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final LikesRepository likesRepository;
    private final CommentRepository commentRepository;
    private final PostMapper postMapper;

    // select ... from post - 1건
    // select count(...) from post ... - 1건
    // select count(...) from comment where post_id = ? 1건
    // select count(...) from likes where post_id = ? - 1건
    // 게시글이 10개.  2건 + (1건 * 2) * 10 = 22건 -> 2N
    public PageResponse<PostResponse> getAllPosts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Post> postPage = postRepository.findByIsVisibleTrue(pageDetails);
        List<Post> posts = postPage.getContent();
        return PageResponse.of(postPage, toResponseListWithCounts(posts));
    }

    public PageResponse<PostResponse> getAllPostsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Post> postPage = postRepository.findAdminBy(pageDetails);
        List<Post> posts = postPage.getContent();
        return PageResponse.of(postPage, toResponseListWithCounts(posts));
    }

    public PageResponse<PostResponse> getMyPosts(Long memberId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_MY_FIELDS);
        Page<Post> postPage = postRepository.findByMemberId(memberId, pageDetails);
        List<Post> posts = postPage.getContent();
        return PageResponse.of(postPage, toResponseListWithCounts(posts));
    }

    public PostResponse getPostById(Long memberId, Long postId, MemberRole role) {
        Post post = postRepository.findWithMemberAndCategoryById(postId)
                .orElseThrow(PostNotFoundException::new);

        boolean isAuthor = post.getMember().getId().equals(memberId);
        boolean isAdmin = role == MemberRole.ADMIN;

        // 감춘 글은 작성자 본인이나 관리자만 볼 수 있다.
        if (!post.getIsVisible() && !isAuthor && !isAdmin) {
            throw new PostNotFoundException();
        }

        return toResponseWithCounts(post);
    }

    @Transactional
    public PostResponse createPost(Long memberId, PostRequest postRequest) {
        Member member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Category category = categoryRepository.findById(postRequest.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);

        Post postEntity = postMapper.toEntity(postRequest);
        postEntity.setMember(member);
        postEntity.setCategory(category);

        Post savedPost = postRepository.save(postEntity);
        return toResponseWithCounts(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long memberId, Long postId, PostRequest postRequest) {
        Post existingPost = findOwnPost(memberId, postId);

        if (postRequest.getCategoryId() != null
                && !postRequest.getCategoryId().equals(existingPost.getCategory().getId())) {
            Category category = categoryRepository.findById(postRequest.getCategoryId())
                    .orElseThrow(CategoryNotFoundException::new);
            existingPost.setCategory(category);
        }

        postMapper.updateEntityFromRequest(existingPost, postRequest);
        return toResponseWithCounts(existingPost);
    }

    @Transactional
    public PostResponse updatePostVisibility(Long postId, PostVisibilityRequest postVisibilityRequest) {
        Post existingPost = postRepository.findWithMemberAndCategoryById(postId)
                .orElseThrow(PostNotFoundException::new);

        existingPost.setIsVisible(postVisibilityRequest.getIsVisible());
        return toResponseWithCounts(existingPost);
    }

    // 1. 게시글을 삭제할 때 물리 삭제
    // 1.1 댓글을 먼저 지우고, 좋아요를 지우고, 마지막으로 게시글을 지운다.
    // 1.1.1 A라는 사람이 B라는 사람의 게시글의 댓글을 달았어요.
    // 1.1.2 마이페이지에서는 내가 남긴 댓글 목록이 보이는 페이지가 있다고 가정.
    // 1.2.3 거기서 B라는 사람의 게시글에 남긴 댓글이 보이고 있음.
    // 1.2.4 그런데 어느날 B라는 사람이 게시글을 삭제했어요. 그러면 A라는 사람의 댓글은 어떻게 될까요?
    // 1.2.5 A라는 사람의 댓글은 게시글이 삭제되었기 때문에 더 이상 존재하지 않는 게시글에 대한 댓글이 되어버립니다.
    // 1.2.6 따라서 게시글을 삭제할 때 댓글도 함께 삭제해야 합니다.
    // 1.2.7 이렇게 될 경우 A라는 사람 입장에서는 갑자기 내가 보던 댓글 목록의 갯수가 1개 줄어든 것처럼 보이게 됨.

    // 2. 게시글을 삭제할 때 논리 삭제
    // 2.1 deletedAt 컬럼을 추가하고, 게시글을 삭제할 때 deletedAt 컬럼에 현재 시간을 넣어주고,
    //     게시글을 조회할 때 deletedAt 컬럼이 null인 게시글만 조회하도록 한다.
    // 2.2 이렇게 하면 게시글을 삭제하더라도 댓글은 그대로 남아있게 된다.
    // 2.3 하지만 게시글이 삭제되었기 때문에 댓글은 더 이상 존재하지 않는 게시글에 대한 댓글이 되어버린다.
    // 2.4 따라서 이를 위한 정책 및 비지니스 로직에 대한 고민이 필요하다. (원본 글 삭제됨)
    @Transactional
    public void deletePost(Long memberId, Long postId) {
        Post existingPost = findOwnPost(memberId, postId);
        commentRepository.deleteAllByPostId(postId);
        likesRepository.deleteAllByPostId(postId);
        postRepository.delete(existingPost);
    }

    private Post findOwnPost(Long memberId, Long postId) {
        Post post = postRepository.findWithMemberAndCategoryById(postId)
                .orElseThrow(PostNotFoundException::new);

        if (!post.getMember().getId().equals(memberId)) {
            if (!post.getIsVisible()) {
                throw new PostNotFoundException();
            }

            throw new ForbiddenException("본인이 작성한 게시글만 수정하거나 삭제할 수 있습니다.");
        }

        return post;
    }

    private PostResponse toResponseWithCounts(Post post) {
        PostResponse response = postMapper.toResponse(post);
        response.setCommentCount(commentRepository.countByPostId(post.getId()));
        response.setLikesCount(likesRepository.countByPostId(post.getId()));
        return response;
    }

    private List<PostResponse> toResponseListWithCounts(List<Post> posts) {
        List<PostResponse> responses = postMapper.toResponseList(posts);
        if (responses.isEmpty()) {
            return responses;
        }

        // SELECT POST_ID, COUNT(ID) FROM COMMENT WHERE POST_ID IN (1, 2, 3)  GROUP BY POST_ID;
        List<Long> postIds = responses.stream().map(PostResponse::getId).toList(); // (1, 2, 3)

        // <키, 값> = <게시글 번호, 댓글 수>
        // <1, 3>, <2, 5>, <3, 0>
        Map<Long, Long> commentCounts = toCountMap(commentRepository.countByPostIds(postIds));

        // <키, 값> = <게시글 번호, 좋아요 수>
        // <1, 3>, <2, 5>, <3, 0>
        Map<Long, Long> likesCounts = toCountMap(likesRepository.countByPostIds(postIds));

        // 결과에 없는 게시글은 댓글이나 좋아요가 없는 것이다
        // Map.getOrDefault(key, defaultValue) : key에 해당하는 값이 있으면 그 값을 반환하고, 없으면 defaultValue를 반환한다.
        for (PostResponse response : responses) {
            response.setCommentCount(commentCounts.getOrDefault(response.getId(), 0L));
            response.setLikesCount(likesCounts.getOrDefault(response.getId(), 0L));
        }

        return responses;
    }

    // postId | total
    //  1   | 3
    //  2   | 5
    private Map<Long, Long> toCountMap(List<PostCount> counts) {
        return counts.stream().collect(Collectors.toMap(PostCount::getPostId, PostCount::getTotal));
    }
}
