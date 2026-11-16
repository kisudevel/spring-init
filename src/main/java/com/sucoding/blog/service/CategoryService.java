package com.sucoding.blog.service;

import com.sucoding.blog.dto.category.CategoryRequest;
import com.sucoding.blog.dto.category.CategoryResponse;
import com.sucoding.blog.dto.common.PageResponse;
import com.sucoding.blog.exceptions.custom.CategoryNotFoundException;
import com.sucoding.blog.exceptions.custom.DuplicateCategoryException;
import com.sucoding.blog.mapper.CategoryMapper;
import com.sucoding.blog.model.Category;
import com.sucoding.blog.repository.CategoryRepository;
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
public class CategoryService {

    private static final List<String> SORTABLE_FIELDS = List.of("id", "name");
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public PageResponse<CategoryResponse> getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Pageable pageDetails = PageableFactory.create(pageNumber, pageSize, sortBy, sortOrder, SORTABLE_FIELDS);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        return PageResponse.of(categoryPage, categoryMapper.toResponseList(categories));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        if (categoryRepository.existsByNameIgnoreCase(categoryRequest.getName())) {
            throw new DuplicateCategoryException();
        }

        // Request DTO를 엔티티로 변환하고 저장 (DTO -> Entity)
        Category categoryEntity = categoryMapper.toEntity(categoryRequest);
        Category savedCategory = categoryRepository.save(categoryEntity);

        return categoryMapper.toResponse(savedCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryRequest categoryRequest) {
        Category existCategory = categoryRepository
                .findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        // 이미 존재하는 카테고리 이름으로 변경하지 못하는 예외처리
        boolean nameChanged = !existCategory.getName().equalsIgnoreCase(categoryRequest.getName());
        boolean duplicate = categoryRepository.existsByNameIgnoreCase(categoryRequest.getName());
        if (nameChanged && duplicate) {
            throw new DuplicateCategoryException();
        }

        existCategory.setName(categoryRequest.getName());
        return categoryMapper.toResponse(existCategory);
    }

    @Transactional
    public CategoryResponse deleteCategory(Long categoryId) {
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        categoryRepository.delete(category);
        return categoryMapper.toResponse(category);
    }
}
