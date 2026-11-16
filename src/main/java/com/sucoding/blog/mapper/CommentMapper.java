package com.sucoding.blog.mapper;

import com.sucoding.blog.dto.comment.CommentRequest;
import com.sucoding.blog.dto.comment.CommentResponse;
import com.sucoding.blog.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toEntity(CommentRequest commentRequest);

    @Mapping(target = "authorId", source = "member.id")
    @Mapping(source = "member.nickname", target = "authorNickname")
    CommentResponse toResponse(Comment comment);

    List<CommentResponse> toResponseList(List<Comment> comments);
}
