package com.sucoding.blog.dto.post;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id", "title", "content", "thumbnail", "isVisible", "createdAt", "authorId", "authorNickname", "categoryId", "categoryName", "commentCount", "likesCount"})
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private Boolean isVisible;
    private String createdAt;

    private Long authorId;            // 작성자 프로필 링크용
    private String authorNickname;    // 화면에 표시
    private Long categoryId;
    private String categoryName;

    private Long commentCount;
    private Long likesCount;
}
