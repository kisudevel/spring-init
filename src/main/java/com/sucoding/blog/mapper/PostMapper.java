package com.sucoding.blog.mapper;

import com.sucoding.blog.dto.post.PostRequest;
import com.sucoding.blog.dto.post.PostResponse;
import com.sucoding.blog.model.Post;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Post toEntity(PostRequest postRequest);

    @Mapping(source = "member.id", target = "authorId")
    @Mapping(source = "member.nickname", target = "authorNickname")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "commentCount", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    PostResponse toResponse(Post post);

    List<PostResponse> toResponseList(List<Post> posts);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(@MappingTarget Post existingPost, PostRequest postRequest);
}
