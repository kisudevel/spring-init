package com.sucoding.blog.mapper;

import com.sucoding.blog.dto.category.CategoryRequest;
import com.sucoding.blog.dto.category.CategoryResponse;
import com.sucoding.blog.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    Category toEntity(CategoryRequest categoryRequest);

    CategoryResponse toResponse(Category savedCategory);

    List<CategoryResponse> toResponseList(List<Category> categories);

}
