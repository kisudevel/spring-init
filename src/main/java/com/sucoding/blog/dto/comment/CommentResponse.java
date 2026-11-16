package com.sucoding.blog.dto.comment;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({"id", "content", "authorNickname", "createdAt"})
public class CommentResponse {
    private Long id;
    private String content;
    private Long authorId;
    private String authorNickname;
    private String createdAt;
}
